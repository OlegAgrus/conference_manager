package com.agrus.conference_manager.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "Conference_Room")
public class ConferenceRoom {

    @Id
    private String id;

    private String roomName;

    private String location;

    private int maxSeats;

}
