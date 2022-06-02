package com.company.service;

import com.company.dto.SmsDTO;
import com.company.dto.request.CardRequestDTO;
import com.company.dto.response.CardResponseDTO;
import com.company.entity.CardEntity;
import com.company.entity.ProfileEntity;
import com.company.entity.SmsEntity;
import com.company.enums.CardStatus;
import com.company.enums.ProfileStatus;
import com.company.enums.SmsStatus;
import com.company.exception.AppBadRequestException;
import com.company.exception.InsufficientFundsException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.CardRepository;
import com.company.repository.SmsRepository;
import com.company.service.integration.UzcardCardService;
import com.company.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UzcardCardService uzcardCardService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SmsRepository smsRepository;

    public CardResponseDTO create(CardRequestDTO requestDTO, String profileId) {
        CardResponseDTO cardResponseDTO = uzcardCardService.getCardByNumber(requestDTO.getNumber());

        if (!cardResponseDTO.getStatus().equals(CardStatus.ACTIVE)) {
            throw new AppBadRequestException("Card Not Active");
        }

        if (!DateUtil.checkExpiredDate(requestDTO.getExpDate(), cardResponseDTO.getExpiryDate())) {
            throw new AppBadRequestException("Expired date wrong");
        }

        CardEntity entity = new CardEntity();
        entity.setName(requestDTO.getName());
        entity.setNumber(cardResponseDTO.getNumber());
        entity.setExpiryDate(cardResponseDTO.getExpiryDate());
        entity.setProfileId(profileId);
        entity.setStatus(CardStatus.NOT_VERIFIED);
        entity.setPhone(cardResponseDTO.getPhone());

        cardRepository.save(entity);
        smsService.sendSms(cardResponseDTO.getPhone());
        return toDTO(entity);
    }

    public CardResponseDTO getById(String id) {
        CardEntity entity = cardRepository.findByIdAndStatus(id, CardStatus.ACTIVE).orElseThrow(() -> {
            log.warn("Card not found");
            throw new ItemNotFoundException("Card not found");
        });
        return toDTO(entity);
    }

    public List<CardResponseDTO> getCardListByProfileId(String profileId) {
        List<CardResponseDTO> dtoList = new ArrayList<>();
        cardRepository.findAllByProfileId(profileId).forEach(card -> {
            dtoList.add(toDTO(card));
        });
        return dtoList;
    }

    public CardEntity getByPhone(String phone) {
        return cardRepository.findByPhoneAndVisible(phone, true).orElseThrow(() -> {
            log.warn("Card not found");
            throw new ItemNotFoundException("Card not found");
        });
    }

    public CardEntity get(String id) {
        CardEntity entity = cardRepository.findByIdAndStatus(id, CardStatus.ACTIVE).orElseThrow(() -> {
            log.warn("Card not found");
            throw new ItemNotFoundException("Card not found");
        });
        return entity;
    }

    public CardEntity get(String id, Long amount) {
        CardEntity entity = cardRepository.findByIdAndStatus(id, CardStatus.ACTIVE).orElseThrow(() -> {
            log.warn("Card not found");
            throw new ItemNotFoundException("Card not found");
        });
        if (entity.getBalance() < amount) {
            throw new InsufficientFundsException("Balance not Found");
        }
        return entity;
    }

    public CardResponseDTO getByCardNumber(String id) {
        CardEntity entity = cardRepository.findByNumber(id).orElseThrow(() -> {
            log.warn("Card not found");
            throw new ItemNotFoundException("Card not found");
        });
        return toDTO(entity);
    }

    public Long getBalance(String number) {
        return cardRepository.getBalance(number).orElseThrow(() -> {
            log.warn("Card number not found");
            throw new ItemNotFoundException("Card number not found");
        });
    }

    public Boolean paymentMinus(Long amount, String cid) {
        int n = cardRepository.paymentMinus(amount, cid);
        return n > 0;
    }

    public Boolean paymentPlus(Long amount, String cid) {
        int n = cardRepository.paymentPlus(amount, cid);
        return n > 0;
    }

    public Boolean assignPhone(String phone, String cid) {
        int n = cardRepository.assignPhone(phone, cid);
        return n > 0;
    }


    public Boolean changeStatus(CardStatus status, String id) {
        int n = cardRepository.chengStatus(status, id);
        return n > 0;
    }

    public Boolean activation(SmsDTO dto) {
        CardEntity entity = getByPhone(dto.getPhone());
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

        int n = cardRepository.activation(CardStatus.ACTIVE, dto.getPhone());
        return n > 0;
    }

    public void resendSmsCode(String phone) {
        smsService.sendSms(phone);
    }

    private CardResponseDTO toDTO(CardEntity entity) {
        CardResponseDTO responseDTO = new CardResponseDTO();
        responseDTO.setId(entity.getId());
        responseDTO.setNumber(entity.getNumber());
        responseDTO.setCreatedDate(entity.getCreatedDate());
        responseDTO.setStatus(entity.getStatus());
        responseDTO.setExpiryDate(entity.getExpiryDate());
        // responseDTO.setBalance(entity.getBalance());
        return responseDTO;
    }
}
