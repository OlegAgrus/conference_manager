package com.agrus.conference_manager.controller;

import com.agrus.conference_manager.model.Participant;
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

import java.util.Date;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AuthControllerTest {

    private ParticipantRepository participantRepository;
    private PasswordEncoder passwordEncoder;

    private String userFullName = "John Smith";
    private String userPassword = "password";
    private Long userDate = 1580647710000L;
    private String userEmail = "johnsmith@gmail.com";

    @Autowired
    AuthControllerTest(
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
    void login() {
        given().auth()
                .form(
                        userEmail,
                        userPassword,
                        new FormAuthConfig("/perform_login", "username", "password"))
                .when().post("/api/v1/login")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void correctRegisterWhenEmailIsUnique() {
        String registerJson = "{\n\"fullName\": \"Jack\",\n\"email\": \"jack@gmail.com\",\n\"password\": \"password\",\n\"birthDate\": \"2020-02-01\"\n}";

        try {
            given()
                    .contentType(ContentType.JSON)
                    .body(registerJson)
            .when().post("/api/v1/register")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("fullName", equalTo("Jack"))
                    .body("email", equalTo("jack@gmail.com"))
                    .body("password", equalTo(null))
                    .body("birthDate", equalTo("2020-02-01T00:00:00.000+0000"));
        }
        finally {
            participantRepository.findByEmail("jack@gmail.com")
                    .ifPresent(participantToDelete -> participantRepository.delete(participantToDelete));
        }
    }

    @Test
    void incorrectRegisterWhenEmailIsNotUnique() {
        String registerJson = "{\n\"fullName\": \"Jack\",\n\"email\": \"" + userEmail + "\",\n\"password\": \"password\",\n\"birthDate\": \"2020-02-01\"\n}";

        given()
                .contentType(ContentType.JSON)
                .body(registerJson)
        .when().post("/api/v1/register")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("user with this email already exists"));
    }
}