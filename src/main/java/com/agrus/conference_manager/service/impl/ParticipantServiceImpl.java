package com.agrus.conference_manager.service.impl;

import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ParticipantRepository;
import com.agrus.conference_manager.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ParticipantServiceImpl implements ParticipantService {

    private ParticipantRepository participantRepository;

    @Autowired
    public ParticipantServiceImpl(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public Participant getCurrentParticipant() {
        //TODO make error
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return participantRepository.findByEmail(authentication.getName())
                .orElse(null);
    }

}
