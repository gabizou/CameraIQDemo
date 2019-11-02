package com.gabizou.cameraiq.demo.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;
import java.util.function.Function;

public class UserImpl extends PersistentEntity<UserCommand, UserEvent,
    Optional<UserEntity>> {
    @Override
    public Behavior initialBehavior(Optional<Optional<UserEntity>> snapshotState) {
        Optional<UserEntity> user = snapshotState.flatMap(Function.identity());

        if (user.isPresent()) {
            return created(user.get());
        } else {
            return notCreated();
        }
    }

}
