package com.agrus.conference_manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Participant {

    @Id
    private Long id;

    private String fullName;

    //@Indexed(unique = true)
    private String email;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

    private String password;
}
