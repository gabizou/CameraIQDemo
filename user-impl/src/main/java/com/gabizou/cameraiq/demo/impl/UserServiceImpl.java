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
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class UserServiceImpl implements UserService {

    private static final String ENTITY_KEY = UserServiceImpl.class.getName();
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
        return request -> this.getUserRef()
            .ask(new UserCommand.GetUser(uuid))
            .thenComposeAsync(done -> this.repo.lookupUser(uuid));
    }

    private PersistentEntityRef<UserCommand> getUserRef() {
        return this.registry.refFor(UserEntity.class, UserServiceImpl.ENTITY_KEY);
    }

    @Override
    public ServiceCall<UserRegistration, User> createUser() {
        return registrationInfo -> {
            final UUID uuid = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_OID, registrationInfo.email);
            final User newUser = new User(new UserId(uuid), registrationInfo);

            return this.repo.saveUser(newUser)
                .thenComposeAsync(saved -> this.getUserRef()
                    .ask(new UserCommand.CreateUser(saved))
                );
        };
    }

    @Override
    public ServiceCall<NotUsed, POrderedSet<User>> getUsers() {
        return none -> this.repo.getUsers();
    }

    @Override
    public ServiceCall<Set<UserId>, Pair<POrderedSet<User>, Set<String>>> getUsersByIds() {
        return userIds -> {
            final OrderedPSet<ServiceCall<NotUsed, User>> serviceCalls = userIds.parallelStream()
                .map(userId -> userId.uuid)
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

}
