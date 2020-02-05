package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.model.Conference;
import com.agrus.conference_manager.model.ConferenceRoom;
import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ConferenceRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ConferenceControllerTest {

    private ParticipantRepository participantRepository;
    private PasswordEncoder passwordEncoder;
    private ConferenceRoomRepository conferenceRoomRepository;
    private ConferenceRepository conferenceRepository;

    private String userFullName = "John Smith";
    private String userPassword = "password";
    private Long userDate = 1580647710000L;
    private String userEmail = "johnsmith@gmail.com";

    @Autowired
    ConferenceControllerTest(
            ParticipantRepository participantRepository,
            PasswordEncoder passwordEncoder,
            ConferenceRoomRepository conferenceRoomRepository,
            ConferenceRepository conferenceRepository
    ) {
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
        this.conferenceRoomRepository = conferenceRoomRepository;
        this.conferenceRepository = conferenceRepository;
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
    void getConferenceList() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        try {
            Conference[] conferences = given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .get("/api/v1/conference/")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(Conference[].class);

            List<Conference> conferenceList = Arrays.asList(conferences);
            assertTrue(conferenceList.contains(conference));
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_GetConferenceByCorrectId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .get("/api/v1/conference/" + conference.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("name", equalTo("conference_name"))
                    .body("conferenceDate", equalTo(1580649710000L))
                    .body("conferenceRoom.roomName", equalTo("new_room_1"))
                    .body("owner.email", equalTo(userEmail))
                    .body("owner.password", equalTo(null))
                    .body("participants[0].email", equalTo(userEmail))
                    .body("participants[0].password", equalTo(null));
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_GetConferenceByIncorrectId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        conferenceRoomRepository.delete(conferenceRoom);
        conferenceRepository.delete(conference);

        given()
                .auth()
                .form(
                        userEmail,
                        userPassword,
                        new FormAuthConfig(
                                "/perform_login",
                                "username",
                                "password"))
                .get("/api/v1/conference/" + conference.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("conference doesn't exist by id"));
    }

    @Test
    void test_CreateConferenceWithExistingConferenceRoom() {
        String createConferenceJson = "{\n    \"name\": \"new_conference_room\",\n    \"conferenceDate\": 1577877060000,\n    \"participants\": [],\n    \"conferenceRoom\": {\n        \"id\": \"4\"\n    }\n}";
        Conference conference = null;

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .id("4")
                .roomName("conference_room")
                .location("first floor...")
                .maxSeats(12)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        try {
            conference = given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .contentType(ContentType.JSON)
                    .body(createConferenceJson)
                    .post("/api/v1/conference")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("name", equalTo("new_conference_room"))
                    .body("conferenceDate", equalTo(1577877060000L))
                    .body("conferenceRoom.roomName", equalTo("conference_room"))
                    .body("owner.email", equalTo(userEmail))
                    .body("owner.password", equalTo(null))
                    .extract()
                    .as(Conference.class);

        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            if (conference != null) conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_CreateConferenceWithNonExistingConferenceRoom() {
        String createConferenceJson = "{\n    \"name\": \"new_conference_room\",\n    \"conferenceDate\": 1577877060000,\n    \"participants\": [],\n    \"conferenceRoom\": {\n        \"id\": \"4\"\n    }\n}";

        given()
                .auth()
                .form(
                        userEmail,
                        userPassword,
                        new FormAuthConfig(
                                "/perform_login",
                                "username",
                                "password"))
                .contentType(ContentType.JSON)
                .body(createConferenceJson)
                .post("/api/v1/conference")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("conference room doesn't exist by id"));
    }

    @Test
    void test_CancelConferenceWithExistingId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .delete("/api/v1/conference/" + conference.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("name", equalTo("conference_name"))
                    .body("conferenceDate", equalTo(1580649710000L))
                    .body("conferenceRoom.roomName", equalTo("new_room_1"))
                    .body("owner.email", equalTo(userEmail))
                    .body("owner.password", equalTo(null))
                    .body("participants[0].email", equalTo(userEmail))
                    .body("participants[0].password", equalTo(null));
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_CancelConferenceWithNonExistingId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        conferenceRoomRepository.delete(conferenceRoom);
        conferenceRepository.delete(conference);

        given()
                .auth()
                .form(
                        userEmail,
                        userPassword,
                        new FormAuthConfig(
                                "/perform_login",
                                "username",
                                "password"))
                .delete("/api/v1/conference/" + conference.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("conference doesn't exist by id"));
    }

    @Test
    void test_CancelConferenceWithNoAccess() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);
        assert participant != null;
        Participant fakeOwner = Participant.builder()
                .fullName(participant.getFullName())
                .birthDate(participant.getBirthDate())
                .email("fake@gmail.com")
                .password(null)
                .build();
        fakeOwner = participantRepository.save(fakeOwner);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(fakeOwner)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .delete("/api/v1/conference/" + conference.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("message", equalTo("you have not access to this action"));
        }
        finally {
            participantRepository.delete(fakeOwner);
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_AddParticipantWithExistingId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(new ArrayList<>())
                .build();
        conference = conferenceRepository.save(conference);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .put("/api/v1/conference/add/" + conference.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("name", equalTo("conference_name"))
                    .body("conferenceDate", equalTo(1580649710000L))
                    .body("conferenceRoom.roomName", equalTo("new_room_1"))
                    .body("owner.email", equalTo(userEmail))
                    .body("owner.password", equalTo(null))
                    .body("participants[0].email", equalTo(userEmail))
                    .body("participants[0].password", equalTo(null));
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_AddParticipantWithNonExistingId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        conferenceRoomRepository.delete(conferenceRoom);
        conferenceRepository.delete(conference);

        given()
                .auth()
                .form(
                        userEmail,
                        userPassword,
                        new FormAuthConfig(
                                "/perform_login",
                                "username",
                                "password"))
                .put("/api/v1/conference/add/" + conference.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("conference doesn't exist by id"));
    }

    @Test
    void test_AddParticipantWithNoRoomSeats() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(1)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .put("/api/v1/conference/add/" + conference.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("message", equalTo("there is no free seats in conference room"));
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_RemoveParticipantWithExistingId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .put("/api/v1/conference/delete/" + conference.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("name", equalTo("conference_name"))
                    .body("conferenceDate", equalTo(1580649710000L))
                    .body("conferenceRoom.roomName", equalTo("new_room_1"))
                    .body("owner.email", equalTo(userEmail))
                    .body("owner.password", equalTo(null))
                    .body("participants", empty());
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_RemoveParticipantWithNonExistingId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        conferenceRoomRepository.delete(conferenceRoom);
        conferenceRepository.delete(conference);

        given()
                .auth()
                .form(
                        userEmail,
                        userPassword,
                        new FormAuthConfig(
                                "/perform_login",
                                "username",
                                "password"))
                .put("/api/v1/conference/delete/" + conference.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("conference doesn't exist by id"));
    }

    @Test
    void test_KickParticipantWithExistingIds() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);
        assert participant != null;

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .put("/api/v1/conference/delete/" + conference.getId() + "/participant/" + participant.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("name", equalTo("conference_name"))
                    .body("conferenceDate", equalTo(1580649710000L))
                    .body("conferenceRoom.roomName", equalTo("new_room_1"))
                    .body("owner.email", equalTo(userEmail))
                    .body("owner.password", equalTo(null))
                    .body("participants", empty());
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_KickParticipantWithNonExistingConferenceId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);
        assert participant != null;

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        conferenceRoomRepository.delete(conferenceRoom);
        conferenceRepository.delete(conference);

        given()
                .auth()
                .form(
                        userEmail,
                        userPassword,
                        new FormAuthConfig(
                                "/perform_login",
                                "username",
                                "password"))
                .put("/api/v1/conference/delete/" + conference.getId() + "/participant/" + participant.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("conference doesn't exist by id"));
    }

    @Test
    void test_KickParticipantWithNonExistingParticipantId() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);
        assert participant != null;

        Participant fakeOwner = Participant.builder()
                .fullName(participant.getFullName())
                .birthDate(participant.getBirthDate())
                .email("fake@gmail.com")
                .password(null)
                .build();
        fakeOwner = participantRepository.save(fakeOwner);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(participant)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        participantRepository.delete(fakeOwner);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .put("/api/v1/conference/delete/" + conference.getId() + "/participant/" + fakeOwner.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("message", equalTo("participant doesn't exist by id"));
        }
        finally {
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

    @Test
    void test_KickParticipantWithNoAccess() {
        Participant participant = participantRepository.findByEmail(userEmail).orElse(null);
        assert participant != null;

        Participant fakeOwner = Participant.builder()
                .fullName(participant.getFullName())
                .birthDate(participant.getBirthDate())
                .email("fake@gmail.com")
                .password(null)
                .build();
        fakeOwner = participantRepository.save(fakeOwner);

        ConferenceRoom conferenceRoom = ConferenceRoom.builder()
                .roomName("new_room_1")
                .location("Second floor...")
                .maxSeats(13)
                .build();
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);

        Conference conference = Conference.builder()
                .name("conference_name")
                .conferenceDate(new Date(1580649710000L))
                .owner(fakeOwner)
                .conferenceRoom(conferenceRoom)
                .participants(Collections.singletonList(participant))
                .build();
        conference = conferenceRepository.save(conference);

        try {
            given()
                    .auth()
                    .form(
                            userEmail,
                            userPassword,
                            new FormAuthConfig(
                                    "/perform_login",
                                    "username",
                                    "password"))
                    .put("/api/v1/conference/delete/" + conference.getId() + "/participant/" + fakeOwner.getId())
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("message", equalTo("you have not access to this action"));
        }
        finally {
            participantRepository.delete(fakeOwner);
            conferenceRoomRepository.delete(conferenceRoom);
            conferenceRepository.delete(conference);
        }
    }

}