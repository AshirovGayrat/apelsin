package com.company.service;

import com.company.dto.RegistrationDto;
import com.company.dto.SmsDTO;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileRole;
import com.company.enums.ProfileStatus;
import com.company.exp.AppBadRequestException;
import com.company.exp.ItemAlreadyExistsException;
import com.company.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AttachService attachService;
    private final ProfileService profileService;


    public void registration(RegistrationDto dto) {
        Optional<ProfileEntity> optional = profileRepository.findByPhone(dto.getPhone());
        if (optional.isPresent()) {
            log.warn("Phone already axists : {}", dto);
            throw new ItemAlreadyExistsException("Phone already exists!");
        }

        ProfileEntity entity = toProfileEntity(dto);
        try {
            profileRepository.save(entity);
        }catch (DataIntegrityViolationException e){
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                sendVerificationEmail(entity);
//            }
//        };
//        thread.start();
    }

    public Boolean activisation(SmsDTO dto){

        return true;
    }

    public ProfileEntity toProfileEntity(RegistrationDto dto) {
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhone(dto.getPhone());
        entity.setRole(ProfileRole.USER);
        entity.setStatus(ProfileStatus.NOTACTIVE);
        return entity;
    }
}
