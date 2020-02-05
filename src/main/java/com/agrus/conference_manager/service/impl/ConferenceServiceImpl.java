package com.agrus.conference_manager.service.impl;

import com.agrus.conference_manager.dto.ConferenceDto;
import com.agrus.conference_manager.mapper.ConferenceDtoModelListMapper;
import com.agrus.conference_manager.mapper.ConferenceDtoModelMapper;
import com.agrus.conference_manager.model.Conference;
import com.agrus.conference_manager.model.ConferenceRoom;
import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ConferenceRepository;
import com.agrus.conference_manager.repository.ConferenceRoomRepository;
import com.agrus.conference_manager.repository.ParticipantRepository;
import com.agrus.conference_manager.service.ConferenceService;
import com.agrus.conference_manager.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConferenceServiceImpl implements ConferenceService {

    private ConferenceRepository conferenceRepository;
    private ConferenceDtoModelMapper conferenceDtoModelMapper;
    private ConferenceDtoModelListMapper conferenceDtoModelListMapper;
    private ParticipantService participantService;
    private ParticipantRepository participantRepository;
    private ConferenceRoomRepository conferenceRoomRepository;

    @Autowired
    public ConferenceServiceImpl(
            ConferenceRepository conferenceRepository,
            ConferenceDtoModelMapper conferenceDtoModelMapper,
            ConferenceDtoModelListMapper conferenceDtoModelListMapper,
            ParticipantService participantService,
            ParticipantRepository participantRepository,
            ConferenceRoomRepository conferenceRoomRepository
    ) {
        this.conferenceRepository = conferenceRepository;
        this.conferenceDtoModelMapper = conferenceDtoModelMapper;
        this.conferenceDtoModelListMapper = conferenceDtoModelListMapper;
        this.participantService = participantService;
        this.participantRepository = participantRepository;
        this.conferenceRoomRepository = conferenceRoomRepository;
    }

    @Override
    public List<ConferenceDto> getConferenceList() {
        List<Conference> conferenceList = conferenceRepository.findAll();
        return conferenceDtoModelListMapper.convertToDtoList(conferenceList);
    }

    @Override
    public ConferenceDto getConference(String conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("conference doesn't exist by id"));
        return conferenceDtoModelMapper.convertToDto(conference);
    }

    @Override
    public ConferenceDto addParticipant(String conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("conference doesn't exist by id"));
        if (!checkConferenceAvailability(conference)) throw new RuntimeException("there is no free seats in conference room");
        Participant currentParticipant = participantService.getCurrentParticipant();
        if (currentParticipant == null) throw new RuntimeException("you are not logged in");
        List<Participant> participantList = conference.getParticipants();
        if (!participantList.contains(currentParticipant)) {
            participantList.add(currentParticipant);
            conferenceRepository.save(conference);
        }
        return conferenceDtoModelMapper.convertToDto(conference);
    }

    @Override
    public ConferenceDto removeParticipant(String conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("conference doesn't exist by id"));
        Participant currentParticipant = participantService.getCurrentParticipant();
        if (currentParticipant == null) throw new RuntimeException("you are not logged in");
        List<Participant> participantList = conference.getParticipants();
        participantList.remove(currentParticipant);
        return conferenceDtoModelMapper.convertToDto(conferenceRepository.save(conference));
    }

    @Override
    public ConferenceDto kickParticipant(String conferenceId, String participantId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("conference doesn't exist by id"));
        if (isNotConferenceOwner(conference)) throw new RuntimeException("you have not access to this action");
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("participant doesn't exist by id"));
        List<Participant> participantList = conference.getParticipants();
        participantList.remove(participant);
        return conferenceDtoModelMapper.convertToDto(conferenceRepository.save(conference));
    }

    @Override
    public ConferenceDto createConference(ConferenceDto conferenceDto) {
        Participant owner = participantService.getCurrentParticipant();
        if (owner == null) throw new RuntimeException("you are not logged in");
        ConferenceRoom conferenceRoom = conferenceRoomRepository.findById(conferenceDto.getConferenceRoom().getId())
                .orElseThrow(() -> new RuntimeException("conference room doesn't exist by id"));
        Conference conference = Conference.builder()
                .name(conferenceDto.getName())
                .owner(owner)
                .participants(new ArrayList<>())
                .conferenceRoom(conferenceRoom)
                .conferenceDate(conferenceDto.getConferenceDate())
                .build();
        return conferenceDtoModelMapper.convertToDto(conferenceRepository.save(conference));
    }

    @Override
    public ConferenceDto deleteConference(String conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("conference doesn't exist by id"));
        if (isNotConferenceOwner(conference)) throw new RuntimeException("you have not access to this action");
        conferenceRepository.delete(conference);
        return conferenceDtoModelMapper.convertToDto(conference);
    }

    private boolean checkConferenceAvailability(Conference conference) {
        return conference.getParticipants().size() < conference.getConferenceRoom().getMaxSeats();
    }

    private boolean isNotConferenceOwner(Conference conference) {
        Participant participant = participantService.getCurrentParticipant();
        return !participant.getId().equals(conference.getOwner().getId());
    }

}
