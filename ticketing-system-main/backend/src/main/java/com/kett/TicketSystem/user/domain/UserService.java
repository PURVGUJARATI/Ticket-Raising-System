package com.kett.TicketSystem.user.domain;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.repository.UserRepository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        EmailAddress emailAddress = EmailAddress.of(email);
        return userRepository.findByEmailEquals(emailAddress).orElse(null);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.removeById(id);
    }
    
}