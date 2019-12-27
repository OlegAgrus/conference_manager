package com.agrus.conference_manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Conference_Room")
public class ConferenceRoom {

    @Id
    private Long id;

    private String roomName;

    private String location;

    private int maxSeats;

}
