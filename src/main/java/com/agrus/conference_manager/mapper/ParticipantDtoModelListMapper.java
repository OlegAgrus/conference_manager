package com.agrus.conference_manager.mapper;

import com.agrus.conference_manager.dto.ParticipantDto;
import com.agrus.conference_manager.model.Participant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParticipantDtoModelListMapper implements DtoModelListMapper<ParticipantDto, Participant> {

    @Override
    public ParticipantDto convertToDto(Participant participant) {
        return ParticipantDto.builder()
                .id(participant.getId())
                .fullName(participant.getFullName())
                .email(participant.getEmail())
                .birthDate(participant.getBirthDate())
                .password(null)
                .build();
    }

    @Override
    public Participant convertToModel(ParticipantDto participantDto) {
        return Participant.builder()
                .id(participantDto.getId())
                .fullName(participantDto.getFullName())
                .email(participantDto.getEmail())
                .birthDate(participantDto.getBirthDate())
                .password(null)
                .build();
    }

    @Override
    public List<ParticipantDto> convertToDtoList(List<Participant> participants) {
        return participants.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<Participant> convertToModelList(List<ParticipantDto> participantDtos) {
        return participantDtos.stream().map(this::convertToModel).collect(Collectors.toList());
    }

}
