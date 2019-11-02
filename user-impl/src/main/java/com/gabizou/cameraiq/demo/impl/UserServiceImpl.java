package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserRegistration;
import com.gabizou.cameraiq.demo.api.UserService;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PSequence;

import javax.annotation.Nullable;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final PersistentEntityRegistry registry;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private final UserRepository userRepository;

    @Inject
    public UserServiceImpl(PersistentEntityRegistry registry, UserRepository userRepository) {
        this.registry = registry;
        this.userRepository = userRepository;
        registry.register(PUserEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, User> user(final UUID id) {
        return null;
    }

    @Override
    public ServiceCall<UserRegistration, User> createUser() {
        return user -> {
       };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<User>> getUsers(@Nullable final Integer pageNo, @Nullable final Integer pageSize) {
        return null;
    }

    @Override
    public Topic<UserEvent> userEvents() {
        return null;
    }
}
