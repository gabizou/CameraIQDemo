package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.datastax.driver.core.Statement;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserRegistration;
import com.gabizou.cameraiq.demo.api.UserService;
import com.gabizou.cameraiq.demo.impl.repo.UserRepository;
import com.gabizou.cameraiq.demo.util.UUIDType5;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcollections.POrderedSet;

import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final PersistentEntityRegistry registry;
    private final UserRepository repo;

    @Inject
    public UserServiceImpl(PersistentEntityRegistry registry,
                           UserRepository repository) {
        this.registry = registry;
        registry.register(UserEntity.class);
        this.repo = repository;
    }

    @Override
    public ServiceCall<NotUsed, User> lookupUser(UUID uuid) {
        return request -> {
            return this.repo.lookupUser(uuid);
        };
    }

    @Override
    public ServiceCall<UserRegistration, User> createUser() {
        return registrationInfo -> {
            final PersistentEntityRef<UserCommand> userCommandPersistentEntityRef = this.registry.refFor(UserEntity.class, registrationInfo.entityID());
            final UUID uuid = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_OID, registrationInfo.email);
            final User newUser = new User(uuid, registrationInfo);
            System.err.println(String.format("Created new User{%s, %s}", uuid, registrationInfo));
            // TODO - Save the new created user
            return userCommandPersistentEntityRef
                .ask(new UserCommand.CreateUser(newUser))
                .thenApplyAsync(this.repo::saveUser);
        };
    }

    @Override
    public ServiceCall<NotUsed, POrderedSet<User>> getUsers() {
        return none -> this.repo.getUsers();
    }
}
