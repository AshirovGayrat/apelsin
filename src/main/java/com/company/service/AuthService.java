package com.company.service;

import com.company.dto.RegistrationDto;
import com.company.dto.SmsDTO;
import com.company.dto.request.AuthDTO;
import com.company.dto.response.ProfileResponseDTO;
import com.company.entity.AttachEntity;
import com.company.entity.ProfileEntity;
import com.company.entity.SmsEntity;
import com.company.enums.ProfileRole;
import com.company.enums.ProfileStatus;
import com.company.enums.SmsStatus;
import com.company.exception.AppBadRequestException;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemAlreadyExistsException;
import com.company.exception.PasswordOrPhoneWrongException;
import com.company.repository.ProfileRepository;
import com.company.repository.SmsRepository;
import com.company.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private SmsRepository smsRepository;
    @Autowired
    private SmsService smsService;

    public void registration(RegistrationDto dto) {
        Optional<ProfileEntity> optional = profileRepository.findByPhone(dto.getPhone());
        if (optional.isPresent()) {
            log.warn("Phone already axists : {}", dto);
            throw new ItemAlreadyExistsException("Phone already exists!");
        }

        ProfileEntity entity = toProfileEntity(dto);
        try {
            profileRepository.save(entity);
            smsService.sendSms(dto.getPhone());
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
    }

    public Boolean activation(SmsDTO dto) {
        ProfileEntity entity = profileService.getByPhone(dto.getPhone());
        Optional<SmsEntity> optional = smsRepository.findTopByPhoneAndStatusOrderByCreatedDateDesc(dto.getPhone(), SmsStatus.NOT_USED);
        if (optional.isEmpty()) {
            return false;
        }

        SmsEntity smsEntity = optional.get();
        if (!smsEntity.getContent().equals(dto.getSms())) {
            smsRepository.updateSmsStatus(SmsStatus.INVALID, smsEntity.getId());
            throw new AppBadRequestException("Code wrong");
        }
        LocalDateTime extTime = smsEntity.getCreatedDate().plusMinutes(2);
        if (LocalDateTime.now().isAfter(extTime)) {
            smsRepository.updateSmsStatus(SmsStatus.INVALID, smsEntity.getId());

            throw new AppBadRequestException("Time is up");
        }
        smsRepository.updateSmsStatus(SmsStatus.USED, smsEntity.getId());

        int n = profileRepository.activation(ProfileStatus.ACTIVE, dto.getPhone());
        return n > 0;
    }

    public ProfileResponseDTO login(AuthDTO dto) {
        String pswd = DigestUtils.md5Hex(dto.getPassword());

        ProfileEntity profileEntity = profileRepository.findByPhoneAndPassword(dto.getPhone(), pswd);
        if (profileEntity == null) {
            throw new PasswordOrPhoneWrongException("Password or Phone wrong!");
        }
        if (!profileEntity.getStatus().equals(ProfileStatus.ACTIVE)) {
            throw new AppForbiddenException("No Access ");
        }

        ProfileResponseDTO profileDTO = new ProfileResponseDTO();
        profileDTO.setName(profileEntity.getName());
        profileDTO.setSurname(profileEntity.getSurname());
        profileDTO.setPhone(profileEntity.getPhone());
        profileDTO.setJwt(JwtUtil.encode(profileEntity.getId()));
        return profileDTO;
    }

    public void resendSmsCode(String phone) {
        smsService.sendSms(phone);
    }

    public ProfileEntity toProfileEntity(RegistrationDto dto) {
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhone(dto.getPhone());
        entity.setPassword(DigestUtils.md5Hex(dto.getPassword()));
        entity.setRole(ProfileRole.USER);
        entity.setStatus(ProfileStatus.NOT_ACTIVE);
        return entity;
    }
}
