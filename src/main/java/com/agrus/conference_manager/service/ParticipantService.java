package com.agrus.conference_manager.service;

import com.agrus.conference_manager.dto.ParticipantDto;
import com.agrus.conference_manager.model.Participant;

public interface ParticipantService {
    Participant getCurrentParticipant();

    ParticipantDto getCurrentParticipantDto();

    ParticipantDto register(ParticipantDto participantDto);
}
