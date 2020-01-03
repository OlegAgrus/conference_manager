package com.agrus.conference_manager.service.impl;

import com.agrus.conference_manager.dto.ConferenceRoomDto;
import com.agrus.conference_manager.mapper.ConferenceRoomDtoModelListMapper;
import com.agrus.conference_manager.model.ConferenceRoom;
import com.agrus.conference_manager.repository.ConferenceRoomRepository;
import com.agrus.conference_manager.service.ConferenceRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConferenceRoomServiceImpl implements ConferenceRoomService {

    private ConferenceRoomRepository conferenceRoomRepository;
    private ConferenceRoomDtoModelListMapper conferenceRoomMapper;

    @Autowired
    public ConferenceRoomServiceImpl(
            ConferenceRoomRepository conferenceRoomRepository,
            ConferenceRoomDtoModelListMapper conferenceRoomMapper) {
        this.conferenceRoomRepository = conferenceRoomRepository;
        this.conferenceRoomMapper = conferenceRoomMapper;
    }

    @Override
    public ConferenceRoomDto createRoom(ConferenceRoomDto conferenceRoomDto) {
        ConferenceRoom newRoom = conferenceRoomMapper.convertToModel(conferenceRoomDto);
        return conferenceRoomMapper.convertToDto(conferenceRoomRepository.save(newRoom));
    }

    @Override
    public List<ConferenceRoomDto> getList() {
        List<ConferenceRoom> roomList = conferenceRoomRepository.findAll();
        return conferenceRoomMapper.convertToDtoList(roomList);
    }

}
