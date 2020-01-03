package com.agrus.conference_manager.repository;

import com.agrus.conference_manager.model.Conference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConferenceRepository  extends MongoRepository<Conference, Long> {
}
