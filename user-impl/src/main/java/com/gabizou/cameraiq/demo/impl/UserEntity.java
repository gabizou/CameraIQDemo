package com.gabizou.cameraiq.demo.impl;

import akka.Done;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserRegistration;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;


public final class UserEntity extends PersistentEntity<UserCommand, UserEvent, UserState> {


    @Override
    public Behavior initialBehavior(final Optional<UserState> snapshotState) {
        final BehaviorBuilder behavior = this.newBehaviorBuilder(snapshotState.orElse(new UserState(Optional.empty())));
        behavior.setCommandHandler(UserCommand.CreateUser.class, (cmd, ctx) -> {
            if (this.state().user.isPresent()) {
                ctx.invalidCommand("User " + this.entityId() + " is already created");
                return ctx.done();
            }
            final UserRegistration registration = cmd.registration;
            final ArrayList<UserEvent> events = new ArrayList<>();
            // Actually add the user based on registration
            final UUID uuid = UUID.randomUUID();
            final User
                newUser =
                new User(uuid, cmd.registration.firstName, cmd.registration.lastName, cmd.registration.email, cmd.registration.phoneNumber, cmd.registration.address);

            events.add(new UserEvent.UserCreated(uuid, Instant.now()));
            return ctx.thenPersistAll(events, () -> ctx.reply(Done.getInstance()));
        });
        return behavior.build();
    }
}
