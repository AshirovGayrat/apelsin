package com.company.service;

import com.company.dto.AttachSimpleDTO;
import com.company.dto.request.MerchandRequestDTO;
import com.company.dto.response.CardResponseDTO;
import com.company.dto.response.MerchandResponceDTO;
import com.company.entity.MerchandEntity;
import com.company.enums.CardStatus;
import com.company.enums.MerchandStatus;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.MerchandRepository;
import com.company.service.integration.UzcardCardService;
import com.company.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerchandService {
    private final MerchandRepository merchandRepository;
    private final AttachService attachService;
    private final UzcardCardService uzcardCardService;

    public MerchandResponceDTO create(MerchandRequestDTO dto) {
        CardResponseDTO cardResponseDTO = uzcardCardService.getCardByNumber(dto.getCardNumber());

        Optional<MerchandEntity> optional = merchandRepository.findByNameAndVisible(dto.getName(),true);
        if (optional.isPresent()) {
            log.warn("Merchand name already exists");
            throw new ItemNotFoundException("Merchand name already exists");
        }

        if (!cardResponseDTO.getStatus().equals(CardStatus.ACTIVE)) {
            log.warn("Merchand card is not active");
            throw new AppBadRequestException("Card Not Active");
        }

        MerchandEntity entity = new MerchandEntity();
        entity.setName(dto.getName());
        entity.setCardNumber(dto.getCardNumber());
        entity.setStatus(MerchandStatus.ACTIVE);
        entity.setPersentage(dto.getPersent());
        if (dto.getAttachId() != null) {
            entity.setAttachId(dto.getAttachId());
        }

        return toDTO(entity);
    }

    public MerchandResponceDTO update(String id, MerchandRequestDTO dto) {
        if (dto.getName() != null) {
            Optional<MerchandEntity> optional = merchandRepository.findByNameAndVisible(dto.getName(),true);
            if (optional.isPresent()) {
                log.warn("Merchand name already exists");
                throw new ItemNotFoundException("Merchand name already exists");
            }
        }
        MerchandEntity entity = get(id);
        entity.setName(dto.getName());
        entity.setCardNumber(dto.getCardNumber());
        entity.setPersentage(dto.getPersent());
        if (dto.getAttachId() != null) {
            entity.setAttachId(dto.getAttachId());
        }

        return toDTO(entity);
    }

    public MerchandEntity get(String id) {
        return merchandRepository.findByIdAndVisible(id,true).orElseThrow(() -> {
            log.warn("Merchand not found");
            throw new ItemNotFoundException("Merchand not found");
        });
    }

    public Boolean delete(String id) {
        get(id);//check
        merchandRepository.updateVisible(false, id);
        return true;
    }

    public MerchandEntity getByName(String name) {
        return merchandRepository.findByNameAndVisible(name, true).orElseThrow(() -> {
            log.warn("Merchand not found");
            throw new ItemNotFoundException("Merchand not found");
        });
    }

    public PageImpl<MerchandResponceDTO> merchandList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<MerchandEntity> pages = merchandRepository.findAllByVisible(true, pageable);
        List<MerchandEntity> entityList = pages.getContent();

        List<MerchandResponceDTO> dtoList = new LinkedList<>();
        entityList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });
        return new PageImpl<MerchandResponceDTO>(dtoList, pageable, pages.getTotalElements());
    }

    public MerchandResponceDTO toDTO(MerchandEntity entity) {
        MerchandResponceDTO dto = new MerchandResponceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setPersent(entity.getPersentage());
        dto.setCardNumber(entity.getCardNumber());
        if (entity.getAttachId() != null) {
            AttachSimpleDTO attachSimpleDTO = new AttachSimpleDTO();
            attachSimpleDTO.setId(entity.getAttachId());
            attachSimpleDTO.setToOpenUrl(attachService.toOpenUrl(entity.getAttachId()));
            dto.setAttach(attachSimpleDTO);
        }
        return dto;
    }
}
