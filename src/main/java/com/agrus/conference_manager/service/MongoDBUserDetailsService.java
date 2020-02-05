package com.agrus.conference_manager.service;

import com.agrus.conference_manager.model.Participant;
import com.agrus.conference_manager.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MongoDBUserDetailsService implements UserDetailsService {

    private ParticipantRepository participantRepository;

    @Autowired
    public MongoDBUserDetailsService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Participant participant = participantRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found by email"));

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("user"));

        return new User(participant.getEmail(), participant.getPassword(), authorities);
    }
}
