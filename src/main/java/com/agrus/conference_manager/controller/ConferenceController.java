package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.dto.ConferenceDto;
import com.agrus.conference_manager.service.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/conference")
public class ConferenceController {

    private ConferenceService conferenceService;

    @Autowired
    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @GetMapping
    public ResponseEntity<List<ConferenceDto>> getConferenceList() {
        return ResponseEntity
                .ok()
                .body(conferenceService.getConferenceList());
    }

    @GetMapping("/{conferenceId}")
    public ResponseEntity<ConferenceDto> getConference(@PathVariable String conferenceId) {
        return ResponseEntity
                .ok()
                .body(conferenceService.getConference(conferenceId));
    }

    @PostMapping
    public ResponseEntity<ConferenceDto> createConference(@Valid @RequestBody ConferenceDto conferenceDto) {
        return ResponseEntity
                .ok()
                .body(conferenceService.createConference(conferenceDto));
    }

    @DeleteMapping("/{conferenceId}")
    public ResponseEntity<ConferenceDto> cancelConference(@PathVariable String conferenceId) {
        return ResponseEntity
                .ok()
                .body(conferenceService.deleteConference(conferenceId));
    }

    @PutMapping("/add/{conferenceId}")
    public ResponseEntity<ConferenceDto> addParticipant(@PathVariable String conferenceId) {
        return ResponseEntity
                .ok()
                .body(conferenceService.addParticipant(conferenceId));
    }

    @PutMapping("/delete/{conferenceId}")
    public ResponseEntity<ConferenceDto> removeParticipant(@PathVariable String conferenceId) {
        return ResponseEntity
                .ok()
                .body(conferenceService.removeParticipant(conferenceId));
    }

    @PutMapping("/delete/{conferenceId}/participant/{participantId}")
    public ResponseEntity<ConferenceDto> kickParticipant
            (@PathVariable String conferenceId, @PathVariable String participantId) {
        return ResponseEntity
                .ok()
                .body(conferenceService.kickParticipant(conferenceId, participantId));
    }

}
