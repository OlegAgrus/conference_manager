package com.agrus.conference_manager.repository;

import com.agrus.conference_manager.model.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface ParticipantRepository extends MongoRepository<Participant, Long> {

    public Optional<Participant> findByEmail(String email);

}
