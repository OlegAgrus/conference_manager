package com.agrus.conference_manager.service;


import com.agrus.conference_manager.dto.ConferenceRoomDto;

import java.util.List;

public interface ConferenceRoomService {
    ConferenceRoomDto createRoom(ConferenceRoomDto conferenceRoomDto);

    List<ConferenceRoomDto> getList();
}
