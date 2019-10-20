package com.gabizou.cameraiq.demo.service;

import com.gabizou.cameraiq.demo.model.User;
import com.gabizou.cameraiq.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public List<User> getUsers() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public User createUser(String username, String email) {
        final User user = new User(username, null, null, email);
        final List<User> all = repository.findAll(Example.of(user, ExampleMatcher.matchingAny()));
        if (all.stream().filter(usr -> user.getUsername().equalsIgnoreCase(usr.getUsername()))
                .filter(usr -> user.getEmail().equalsIgnoreCase(usr.getEmail()))
                .count() > 0) {
            throw new DuplicateKeyException("");
        }
        repository.save(user);
        return user;
    }

    @Transactional
    public User create( User user) {
        final List<User> all = repository.findAll(Example.of(user, ExampleMatcher.matchingAny()));
        if (all.stream().filter(usr -> user.getUsername().equalsIgnoreCase(usr.getUsername()))
                .filter(usr -> user.getEmail().equalsIgnoreCase(usr.getEmail()))
                .count() > 0) {
            throw new DuplicateKeyException("");
        }
        repository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User findUser( Long userId) {
        final User user = repository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User id" + userId));
        return user;
    }

    /**
     * Update user response entity.
     *
     * @param userid The user id
     * @param user The user details to update
     * @return The reponse entity with the updated details
     */
    @Transactional
    public User updateUser(Long userid, User user) {
        final User found = repository.findById(userid)
            .orElseThrow(() -> new EntityNotFoundException("User not found on " + userid));
        found.setAddress(user.getAddress());
        found.setFirstName(user.getFirstName());
        found.setLastName(user.getLastName());
        found.setEmail(user.getEmail());
        final User updated = repository.save(user);
        return updated;
    }
}
