package com.agrus.conference_manager.service.impl;

import com.agrus.conference_manager.dto.ParticipantDto;
import com.agrus.conference_manager.mapper.ParticipantDtoModelListMapper;
import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ParticipantRepository;
import com.agrus.conference_manager.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ParticipantServiceImpl implements ParticipantService {

    private ParticipantRepository participantRepository;
    private ParticipantDtoModelListMapper participantDtoModelListMapper;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ParticipantServiceImpl(
            ParticipantRepository participantRepository,
            ParticipantDtoModelListMapper participantDtoModelListMapper,
            PasswordEncoder passwordEncoder
            ) {
        this.participantRepository = participantRepository;
        this.participantDtoModelListMapper = participantDtoModelListMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Participant getCurrentParticipant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return participantRepository.findByEmail(authentication.getName())
                .orElse(null);
    }

    @Override
    public ParticipantDto getCurrentParticipantDto() {
        Participant participant = getCurrentParticipant();
        if (participant == null) throw new RuntimeException("you are not logged in");
        return participantDtoModelListMapper.convertToDto(participant);
    }

    @Override
    public ParticipantDto register(ParticipantDto participantDto) {
        Participant existing = participantRepository.findByEmail(participantDto.getEmail()).orElse(null);
        if (existing != null) throw new RuntimeException("user with this email already exists");
        Participant participant = participantDtoModelListMapper.convertToModel(participantDto);
        participant.setPassword(passwordEncoder.encode(participantDto.getPassword()));
        return participantDtoModelListMapper.convertToDto(participantRepository.save(participant));
    }

}
