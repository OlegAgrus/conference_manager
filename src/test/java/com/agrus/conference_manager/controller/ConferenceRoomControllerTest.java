package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.model.ConferenceRoom;
import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ConferenceRoomRepository;
import com.agrus.conference_manager.repository.ParticipantRepository;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.http.ContentType;
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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ConferenceRoomControllerTest {

    private ParticipantRepository participantRepository;
    private PasswordEncoder passwordEncoder;
    private ConferenceRoomRepository conferenceRoomRepository;

    private String userFullName = "John Smith";
    private String userPassword = "password";
    private Long userDate = 1580647710000L;
    private String userEmail = "johnsmith@gmail.com";

    @Autowired
    ConferenceRoomControllerTest(
            ParticipantRepository participantRepository,
            PasswordEncoder passwordEncoder,
            ConferenceRoomRepository conferenceRoomRepository
    ) {
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
        this.conferenceRoomRepository = conferenceRoomRepository;
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
    void correctTest_CreateRoomWithAnyData() {
        String createRoomJson = "{\n    \"roomName\": \"new_room\",\n    \"location\": \"1 floor...\",\n    \"maxSeats\": 12\n}";
        ConferenceRoom conferenceRoom = null;

        try {
            conferenceRoom = given()
                    .auth()
                        .form(
                                userEmail,
                                userPassword,
                                new FormAuthConfig(
                                        "/perform_login",
                                        "username",
                                        "password"))
                    .contentType(ContentType.JSON)
                    .body(createRoomJson)
            .when().post("/api/v1/conference_room")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("roomName", equalTo("new_room"))
                    .body("location", equalTo("1 floor..."))
                    .body("maxSeats", equalTo(12))
                    .extract()
                    .as(ConferenceRoom.class);
        }
        finally {
            if (conferenceRoom != null) conferenceRoomRepository.delete(conferenceRoom);
        }
    }

    @Test
    void getRoomList() {
        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        try {
            ConferenceRoom[] conferenceRooms = given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .when().get("/api/v1/conference_room")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(ConferenceRoom[].class);

            List<ConferenceRoom> conferenceRoomList = Arrays.asList(conferenceRooms);
            assertTrue(conferenceRoomList.contains(conferenceRoom));
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
        }
    }
}