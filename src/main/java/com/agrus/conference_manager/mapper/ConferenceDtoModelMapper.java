package com.agrus.conference_manager.mapper;

import com.agrus.conference_manager.dto.ConferenceDto;
import com.agrus.conference_manager.model.Conference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConferenceDtoModelMapper implements DtoModelMapper<ConferenceDto, Conference> {

    private ConferenceRoomDtoModelMapper conferenceRoomDtoModelMapper;
    private ParticipantDtoModelListMapper participantDtoModelListMapper;

    @Autowired
    public ConferenceDtoModelMapper(
            ConferenceRoomDtoModelMapper conferenceRoomDtoModelMapper,
            ParticipantDtoModelListMapper participantDtoModelListMapper
    ) {
        this.conferenceRoomDtoModelMapper = conferenceRoomDtoModelMapper;
        this.participantDtoModelListMapper = participantDtoModelListMapper;
    }

    @Override
    public ConferenceDto convertToDto(Conference conference) {
        return ConferenceDto.builder()
                .id(conference.getId())
                .name(conference.getName())
                .conferenceDate(conference.getConferenceDate())
                .conferenceRoom(conferenceRoomDtoModelMapper.convertToDto(conference.getConferenceRoom()))
                .participants(participantDtoModelListMapper.convertToDtoList(conference.getParticipants()))
                .owner(participantDtoModelListMapper.convertToDto(conference.getOwner()))
                .build();
    }

    @Override
    public Conference convertToModel(ConferenceDto conferenceDto) {
        return Conference.builder()
                .id(conferenceDto.getId())
                .name(conferenceDto.getName())
                .conferenceDate(conferenceDto.getConferenceDate())
                .conferenceRoom(conferenceRoomDtoModelMapper.convertToModel(conferenceDto.getConferenceRoom()))
                .participants(participantDtoModelListMapper.convertToModelList(conferenceDto.getParticipants()))
                .owner(participantDtoModelListMapper.convertToModel(conferenceDto.getOwner()))
                .build();
    }
}
