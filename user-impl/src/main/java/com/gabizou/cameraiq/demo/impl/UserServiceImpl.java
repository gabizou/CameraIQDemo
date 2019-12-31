package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import akka.japi.Pair;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserId;
import com.gabizou.cameraiq.demo.api.UserRegistration;
import com.gabizou.cameraiq.demo.api.UserService;
import com.gabizou.cameraiq.demo.util.DemoFunctional;
import com.gabizou.cameraiq.demo.util.UUIDType5;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcollections.POrderedSet;
import org.taymyr.lagom.javadsl.openapi.AbstractOpenAPIService;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class UserServiceImpl extends AbstractOpenAPIService implements UserService {

    private static final String ENTITY_KEY = UserServiceImpl.class.getName();
    private final PersistentEntityRegistry registry;
    private final UserRepository repo;
    private static final Logger LOGGER = LogManager.getLogger("UserService");

    @Inject
    public UserServiceImpl(PersistentEntityRegistry registry,
                           UserRepository repository) {
        this.registry = registry;
        registry.register(UserEntity.class);
        this.repo = repository;
    }

    @Override
    public ServiceCall<NotUsed, POrderedSet<User>> getUsers() {
        return none -> this.repo.getUsers();
    }

    @Override
    public ServiceCall<Set<UserId>, Pair<POrderedSet<User>, Set<String>>> getUsersByIds() {
        return userIds -> {
            final POrderedSet<ServiceCall<NotUsed, User>> serviceCalls = userIds.parallelStream()
                .map(this::lookupUser)
                .collect(DemoFunctional.toImmutableSet());
            // Gather all futures to handle, with the exception handling below
            // in the accumilator of our custom collector
            final CompletableFuture<Void> allToProcess =
                CompletableFuture.allOf(serviceCalls.toArray(new CompletableFuture[serviceCalls.size()]));
            return allToProcess.thenApply(v -> serviceCalls.parallelStream()
                .map(ServiceCall::invoke)
                .map(CompletionStage::toCompletableFuture)
                .collect(DemoFunctional.toPairOfObjectAndExceptions()));


        };
    }

    private PersistentEntityRef<UserCommand> getUserRef() {
        return this.registry.refFor(UserEntity.class, UserServiceImpl.ENTITY_KEY);
    }

}
