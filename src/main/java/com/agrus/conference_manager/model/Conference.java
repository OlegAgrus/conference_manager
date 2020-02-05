package com.agrus.conference_manager.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
@EqualsAndHashCode(exclude = {"conferenceRoom", "owner", "participants", "conferenceDate", "name"})
public class Conference {

    @Id
    private String id;

    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date conferenceDate;

    private List<Participant> participants;

    private Participant owner;

    private ConferenceRoom conferenceRoom;

}
