package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.dto.ParticipantDto;
import com.agrus.conference_manager.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/participant")
public class ParticipantController {

    private ParticipantService participantService;

    @Autowired
    public ParticipantController(
            ParticipantService participantService
    ) {
        this.participantService = participantService;
    }

    @GetMapping
    public ResponseEntity<ParticipantDto> getCurrentParticipant() {
        return ResponseEntity
                .ok()
                .body(participantService.getCurrentParticipantDto());
    }

}
