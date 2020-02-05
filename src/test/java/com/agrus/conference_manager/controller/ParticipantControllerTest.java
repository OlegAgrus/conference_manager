package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ConferenceRoomRepository;
import com.agrus.conference_manager.repository.ParticipantRepository;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ParticipantControllerTest {

    private ParticipantRepository participantRepository;
    private PasswordEncoder passwordEncoder;

    private String userFullName = "John Smith";
    private String userPassword = "password";
    private Long userDate = 1580647710000L;
    private String userEmail = "johnsmith@gmail.com";

    @Autowired
    ParticipantControllerTest(
            ParticipantRepository participantRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeAll
    static void setUpGeneral() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @BeforeEach
    void setUp() {
        Participant currentParticipant = Participant.builder()
                .fullName(userFullName)
                .email(userEmail)
                .birthDate(new Date(userDate))
                .password(passwordEncoder.encode(userPassword))
                .build();

        participantRepository.save(currentParticipant);
    }

    @AfterEach
    void tearDown() {
        participantRepository.delete(participantRepository.findByEmail(userEmail).orElse(new Participant()));
    }

    @Test
    void getCurrentParticipant() {
        given()
                .auth()
                .form(
                        userEmail,
                        userPassword,
                        new FormAuthConfig(
                                "/perform_login",
                                "username",
                                "password"))
                .get("/api/v1/participant")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("fullName", equalTo(userFullName))
                .body("email", equalTo(userEmail))
                .body("password", equalTo(null))
                .body("birthDate", equalTo(userDate));
    }
}