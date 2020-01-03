package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.dto.ConferenceRoomDto;
import com.agrus.conference_manager.service.ConferenceRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conference_room")
public class ConferenceRoomController {

    private ConferenceRoomService conferenceRoomService;

    @Autowired
    public ConferenceRoomController(
            ConferenceRoomService conferenceRoomService) {
        this.conferenceRoomService = conferenceRoomService;
    }

    @PostMapping
    public ResponseEntity<ConferenceRoomDto> createRoom(@Valid @RequestBody ConferenceRoomDto conferenceRoomDto) {
        return ResponseEntity
                .ok()
                .body(conferenceRoomService.createRoom(conferenceRoomDto));
    }

    @GetMapping
    public ResponseEntity<List<ConferenceRoomDto>> getRoomList() {
        return ResponseEntity
                .ok()
                .body(conferenceRoomService.getList());
    }

}
