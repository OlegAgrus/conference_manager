package com.agrus.conference_manager.service;

import com.agrus.conference_manager.dto.ConferenceDto;

import java.util.List;

public interface ConferenceService {
    List<ConferenceDto> getConferenceList();

    ConferenceDto getConference(String conferenceId);

    ConferenceDto addParticipant(String conferenceId);

    ConferenceDto removeParticipant(String conferenceId);

    ConferenceDto kickParticipant(String conferenceId, String participantId);

    ConferenceDto createConference(ConferenceDto conferenceDto);

    ConferenceDto deleteConference(String conferenceId);
}
