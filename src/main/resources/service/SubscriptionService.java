package com.company.service;

import com.company.dtoRequest.SubscriptionDTO;
import com.company.entity.SubscriptionEntity;
import com.company.enums.NotificationType;
import com.company.enums.SubscriptionStatus;
import com.company.exp.ItemNotFoundException;
import com.company.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ChannelService channelService;

    public Boolean create(SubscriptionDTO dto, Integer pId) {
        channelService.getById(dto.getChannelId());

        SubscriptionEntity entity = new SubscriptionEntity();
        entity.setChannelId(dto.getChannelId());
        entity.setProfileId(pId);
        entity.setNotificationType(NotificationType.valueOf(dto.getTyp()));
        entity.setStatus(SubscriptionStatus.ACTIVE);

        subscriptionRepository.save(entity);
        return true;
    }

    public Boolean changeStatusOrType(Integer pId, SubscriptionDTO dto) {
        Optional<SubscriptionEntity> optional = subscriptionRepository.findByProfileIdAndChannelId(pId, dto.getChannelId());
        if (optional.isEmpty()) {
            throw new ItemNotFoundException("Subscription not found!");
        }

        SubscriptionEntity entity = optional.get();
        if (dto.getStatus() != null) {
            entity.setStatus(SubscriptionStatus.valueOf(dto.getStatus()));
        }
        if (dto.getTyp() != null) {
            entity.setNotificationType(NotificationType.valueOf(dto.getTyp()));
        }
        subscriptionRepository.save(entity);

        return true;
    }


}
