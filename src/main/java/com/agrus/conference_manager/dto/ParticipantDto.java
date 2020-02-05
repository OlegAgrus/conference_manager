package com.agrus.conference_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDto {

    private String id;

    @NotEmpty(message = "Empty field")
    @NotNull(message = "Empty field")
    private String fullName;

    @NotEmpty(message = "Empty field")
    @NotNull(message = "Empty field")
    @Email(message = "Incorrect email")
    private String email;

    @NotNull(message = "Empty field")
    private Date birthDate;

    @NotEmpty(message = "Empty field")
    @NotNull(message = "Empty field")
    private String password;

}
