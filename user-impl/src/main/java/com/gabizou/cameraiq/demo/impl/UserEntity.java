package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.UserId;
import com.gabizou.cameraiq.demo.impl.events.UserEvent;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcollections.OrderedPSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;


public final class UserEntity extends PersistentEntity<UserCommand, UserEvent, UserState> {

    private static final Logger LOGGER = LogManager.getLogger(UserEntity.class);

    @Override
    public Behavior initialBehavior(final Optional<UserState> snapshotState) {
        final BehaviorBuilder behavior =
            this.newBehaviorBuilder(snapshotState.orElseGet(() -> new UserState(OrderedPSet.empty())));
        behavior.setCommandHandler(UserCommand.CreateUser.class, (cmd, ctx) -> {
            final UserId userId = cmd.registration.userId;
            if (this.state().users.contains(userId)) {
                UserEntity.LOGGER.warn("No user found by id: " + userId);
                ctx.invalidCommand("User " + this.entityId() + " is already created");
                return ctx.done();
            }
            final ArrayList<UserEvent> events = new ArrayList<>();
            // Actually add the user based on registration

            events.add(new UserEvent.UserCreated(userId, Instant.now()));

            return ctx.thenPersistAll(events, () -> ctx.reply(cmd.registration));
        });
        behavior.setEventHandler(UserEvent.UserCreated.class,
            event -> {
                UserEntity.LOGGER.debug("Updating User State with (" + this.state().users.size() + 1 + ") users.");
            return new UserState(this.state().users.plus(event.userId));
            });
        behavior.setReadOnlyCommandHandler(UserCommand.GetUser.class, (cmd, ctx) -> {
            final UserId userId = cmd.uuid;
            if (!this.state().users.contains(userId)) {
                ctx.invalidCommand("User " + cmd.uuid + " does not " + "exist");
            }
            UserEntity.LOGGER.debug("Fetching registered user " + userId);

        });
        return behavior.build();
    }
}
