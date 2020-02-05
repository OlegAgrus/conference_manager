package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.dto.ParticipantDto;
import com.agrus.conference_manager.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthController {

    private ParticipantService participantService;

    @Autowired
    public AuthController(
            ParticipantService participantService
    ) {
        this.participantService = participantService;
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<ParticipantDto> register(@Valid @RequestBody ParticipantDto participantDto) {
        return ResponseEntity
                .ok()
                .body(participantService.register(participantDto));
    }

}
