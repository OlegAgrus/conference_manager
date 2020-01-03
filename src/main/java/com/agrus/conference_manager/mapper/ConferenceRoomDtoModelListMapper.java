package com.agrus.conference_manager.mapper;

import com.agrus.conference_manager.dto.ConferenceRoomDto;
import com.agrus.conference_manager.model.ConferenceRoom;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConferenceRoomDtoModelListMapper implements DtoModelListMapper<ConferenceRoomDto, ConferenceRoom> {

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

    @Override
    public List<ConferenceRoomDto> convertToDtoList(List<ConferenceRoom> conferenceRooms) {
        List<ConferenceRoomDto> resultList = new ArrayList<>(conferenceRooms.size());
        conferenceRooms.forEach((elem) -> resultList.add(convertToDto(elem)));
        return resultList;
    }

    @Override
    public List<ConferenceRoom> convertToModelList(List<ConferenceRoomDto> conferenceRoomDtos) {
        List<ConferenceRoom> resultList = new ArrayList<>(conferenceRoomDtos.size());
        conferenceRoomDtos.forEach((elem) -> resultList.add(convertToModel(elem)));
        return resultList;
    }
}
