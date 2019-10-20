package com.gabizou.cameraiq.demo.controllers;

import com.gabizou.cameraiq.demo.model.User;
import com.gabizou.cameraiq.demo.repositories.UserRepository;
import com.gabizou.cameraiq.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserService service;

    /**
     * Gets all users.
     *
     * @return The list of all users
     */
    @GetMapping("/users")
    @ResponseBody
    public List<User> getAll() {
        return service.getUsers();
    }

    /**
     * Creates a validated {@link User} to be saved.
     *
     * @param user The user to create
     * @return The user
     */
    @PostMapping("/users")
    public User create(@RequestParam("username") String username, @RequestParam("email") String email) {
        return service.createUser(username, email);
    }

    @PostMapping("/users/addUser")
    public User create(@RequestBody User user) {
        return service.create(user);
    }

    /**
     * Gets users by d.
     *
     * @param userId The user id
     * @return The users by id
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> findUser(@PathVariable(value = "id") Long userId) {
        return ResponseEntity.ok(service.findUser(userId));
    }

    /**
     * Update user response entity.
     *
     * @param userid The user id
     * @param user The user details to update
     * @return The reponse entity with the updated details
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userid, @Valid @RequestBody User user) {
        return ResponseEntity.ok(service.updateUser(userid, user));
    }

}
