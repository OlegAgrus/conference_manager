package com.agrus.conference_manager;

import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class ConferenceManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConferenceManagerApplication.class, args);
    }

}
