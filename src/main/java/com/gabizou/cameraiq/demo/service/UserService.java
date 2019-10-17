package com.gabizou.cameraiq.demo.service;

import com.gabizou.cameraiq.demo.model.User;
import com.gabizou.cameraiq.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public List<User> list() {
        return repo.findAll();
    }

}
