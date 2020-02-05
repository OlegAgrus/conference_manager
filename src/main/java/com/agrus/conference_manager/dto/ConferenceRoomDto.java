package com.agrus.conference_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceRoomDto {

    private String id;

    @NotEmpty
    @NotNull
    private String roomName;

    @NotEmpty
    @NotNull
    private String location;

    @NotNull
    @Positive
    private int maxSeats;
}
