package com.gabizou.cameraiq.demo.impl.repo;

import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserRegistration;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.persistence.jpa.JpaSession;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collector;

@Singleton
public class UserRepository {

    public static final String SELECT_ALL_USERS =
        "SELECT NEW com.gabizou.cameraiq.demo.api.User(g.id, g.firstName, g" +
            ".lastName, g.email, g.address, g.phoneNumber) FROM UserJpaEntity g";

    // JpaSession handles transaction recording and rollbacks
    // for us, so we don't have to actually begin new transactions,
    // comitting, and rolling back.
    private final JpaSession session;
    private static final Logger LOGGER =
        LogManager.getLogger(UserRepository.class);

    @Inject
    public UserRepository(final JpaSession session) {
        this.session = session;
    }


    private User storeUser(EntityManager manager, User user) {
        manager.persist(new UserJpaEntity(user));
        return user;
    }


    public CompletionStage<User> lookupUser(final UUID uuid) {
        return this.session.withTransaction(em -> {
            final UserJpaEntity userJpaEntity = em.find(UserJpaEntity.class, uuid);
            if (userJpaEntity == null) {
                throw new IllegalArgumentException("No user found by uuid: " + uuid.toString());
            }
            return new User(userJpaEntity.getId(),
                new UserRegistration(userJpaEntity.getFirstName(),
                    userJpaEntity.getLastName(), userJpaEntity.getAddress(),
                    userJpaEntity.getEmail(), userJpaEntity.getPhoneNumber()));
        });
    }

    public User saveUser(final User user) {
        this.session.withTransaction(em -> {
            final UserJpaEntity jpa = new UserJpaEntity(user);
            em.persist(jpa);
            return jpa;
        });
        return user;
    }

    public CompletionStage<POrderedSet<User>> getUsers() {
        return this.session.withTransaction(em -> em.createQuery(UserRepository.SELECT_ALL_USERS,
            User.class)
            .getResultList()
            .parallelStream()
            .collect(UserRepository.toImmutableSet()));
    }

    public static <T> Collector<T, List<T>, POrderedSet<T>> toImmutableSet() {
        return Collector.of(ArrayList::new, List::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, OrderedPSet::from);
    }
}
