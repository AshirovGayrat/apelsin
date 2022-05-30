package com.company.service;

import com.company.dto.AttachSimpleDTO;
import com.company.dto.AuthDto;
import com.company.dto.ProfileDto;
import com.company.dto.RegistrationDto;
import com.company.entity.AttachEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileRole;
import com.company.enums.ProfileStatus;
import com.company.exp.AppBadRequestException;
import com.company.exp.AppForbiddenException;
import com.company.repository.ProfileRepository;
import com.company.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private EmailService emailService;

    public ProfileDto login(AuthDto dto) {
        String pswd = DigestUtils.md5Hex(dto.getPassword());
        Optional<ProfileEntity> optional = profileRepository.
                findByEmailAndPassword(dto.getEmail(), pswd);

        if (optional.isEmpty()) {
            log.warn("Password or email wrong!: {}", dto);
            throw new PasswordOrEmailWrongException("Password or email wrong!");
        }

        ProfileEntity entity = optional.get();
        if (!entity.getStatus().equals(ProfileStatus.ACTIVE)) {
            log.warn("Not access: {}", dto);
            throw new AppForbiddenException("not access");
        }

        ProfileDto profileDto = new ProfileDto();
        profileDto.setName(entity.getName());
        profileDto.setSurname(entity.getSurname());
        profileDto.setEmail(entity.getEmail());
        profileDto.setRole(entity.getRole());
        profileDto.setJwt(JwtUtil.encode(entity.getId(), entity.getRole()));

        AttachEntity image = entity.getAttach();
        if (image != null) {
            AttachSimpleDTO imageDto = new AttachSimpleDTO();
            imageDto.setUrl(attachService.toOpenURL(image.getId()));
            profileDto.setAttachDto(imageDto);
        }
        return profileDto;
    }

    public void registration(RegistrationDto dto) {
        Optional<ProfileEntity> optional = profileRepository.findByEmail(dto.getEmail());
        if (optional.isPresent()) {
            log.warn("Email already axists : {}", dto);
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        ProfileEntity entity = toProfileEntity(dto);
        profileRepository.save(entity);

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                sendVerificationEmail(entity);
//            }
//        };
//        thread.start();
    }

    public void verification(String jwt) {
        Integer userId = null;
        try {
            userId = JwtUtil.decodeAndGetId(jwt);
        } catch (JwtException e) {
            throw new AppBadRequestException("Verification not completed");
        }
        profileRepository.updateStatus(ProfileStatus.ACTIVE, userId);
    }

    private void sendVerificationEmail(ProfileEntity entity) {
        StringBuilder builder = new StringBuilder();
        String jwt = JwtUtil.encode(entity.getId());
        builder.append("Salom bormisiz \n");
        builder.append("To verify your registration click to next link.");
        builder.append("http://localhost:8080/auth/verification/").append(jwt);
        builder.append("\nMazgi!");
        emailService.send(entity.getEmail(), "Activate Your Registration", builder.toString());
    }

    public ProfileEntity toProfileEntity(RegistrationDto dto) {
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setEmail(dto.getEmail());
        entity.setPassword(DigestUtils.md5Hex(dto.getPassword()));
        entity.setRole(ProfileRole.USER);
        entity.setStatus(ProfileStatus.ACTIVE);
        return entity;
    }
}
