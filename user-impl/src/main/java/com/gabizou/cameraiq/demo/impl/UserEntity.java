package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.UserId;
import com.gabizou.cameraiq.demo.impl.events.UserEvent;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.pcollections.OrderedPSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;


public final class UserEntity extends PersistentEntity<UserCommand, UserEvent, UserState> {

    @Override
    public Behavior initialBehavior(final Optional<UserState> snapshotState) {
        final BehaviorBuilder behavior =
            this.newBehaviorBuilder(snapshotState.orElseGet(() -> new UserState(OrderedPSet.empty())));
        behavior.setCommandHandler(UserCommand.CreateUser.class, (cmd, ctx) -> {
            final UserId userId = new UserId(cmd.registration.uuid);
            if (this.state().users.contains(userId)) {
                ctx.invalidCommand("User " + this.entityId() + " is already created");
                return ctx.done();
            }
            final ArrayList<UserEvent> events = new ArrayList<>();
            // Actually add the user based on registration

            events.add(new UserEvent.UserCreated(userId, Instant.now()));

            return ctx.thenPersistAll(events, () -> ctx.reply(cmd.registration));
        });
        behavior.setEventHandler(UserEvent.UserCreated.class,
            event -> new UserState(this.state().users.plus(event.userId)));
        return behavior.build();
    }
}
