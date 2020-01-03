package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestC {

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public void demo() {
        System.out.println("Test successful");
    }

}
