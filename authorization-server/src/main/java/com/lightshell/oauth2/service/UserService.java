package com.lightshell.oauth2.service;

import com.lightshell.oauth2.entity.User;
import com.lightshell.oauth2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements SuperService<User> {

    @Autowired
    UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsernameEquals(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> saveAll(List<User> data) {
        return userRepository.saveAll(data);
    }

}