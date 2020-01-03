package com.agrus.conference_manager.mapper;

import com.agrus.conference_manager.dto.ConferenceRoomDto;
import com.agrus.conference_manager.model.ConferenceRoom;
import org.springframework.stereotype.Component;

@Component
public class ConferenceRoomDtoModelMapper implements DtoModelMapper<ConferenceRoomDto, ConferenceRoom> {

    @Override
    public ConferenceRoomDto convertToDto(ConferenceRoom conferenceRoom) {
        return ConferenceRoomDto.builder()
                .id(conferenceRoom.getId())
                .location(conferenceRoom.getLocation())
                .roomName(conferenceRoom.getRoomName())
                .maxSeats(conferenceRoom.getMaxSeats())
                .build();
    }

    @Override
    public ConferenceRoom convertToModel(ConferenceRoomDto conferenceRoomDto) {
        return ConferenceRoom.builder()
                .id(conferenceRoomDto.getId())
                .location(conferenceRoomDto.getLocation())
                .roomName(conferenceRoomDto.getRoomName())
                .maxSeats(conferenceRoomDto.getMaxSeats())
                .build();
    }
}
