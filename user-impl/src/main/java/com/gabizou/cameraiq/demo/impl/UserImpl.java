package com.gabizou.cameraiq.demo.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import jnr.posix.util.JavaCrypt;

import java.util.Optional;
import java.util.function.Function;

public class UserImpl extends PersistentEntity<UserCommand, UserEvent,
    Optional<User>> {
    @Override
    public Behavior initialBehavior(Optional<Optional<User>> snapshotState) {
        Optional<User> user = snapshotState.flatMap(Function.identity());

        if (user.isPresent()) {
            return created(user.get());
        } else {
            return notCreated();
        }
    }

}
