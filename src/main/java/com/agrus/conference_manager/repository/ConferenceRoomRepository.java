package com.agrus.conference_manager.repository;

import com.agrus.conference_manager.model.ConferenceRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConferenceRoomRepository  extends MongoRepository<ConferenceRoom, Long> {
}
