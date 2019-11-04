package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.User;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class MembershipEntity extends PersistentEntity<MembershipCommand,
    MembershipEvent, MembershipState> {
    @Override
    public Behavior initialBehavior(final Optional<MembershipState> snapshotState) {
        final BehaviorBuilder builder = this.newBehaviorBuilder(snapshotState.orElseGet(() -> new MembershipState(Optional.empty())));
        builder.setCommandHandler(MembershipCommand.CreateMembership.class,
            (cmd, ctx) -> {
                final User user = cmd.membership.user;
                final Organization org = cmd.membership.organization;
                if (this.state().membership.isPresent()) {
                    final String message = String.format("Membership for %s " +
                        "in Organization %s already exists",
                        user.uuid, org.uuid);
                    ctx.invalidCommand(message);
                    return ctx.done();
                }
                final ArrayList<MembershipEvent> events = new ArrayList<>();
                events.add(new MembershipEvent.CreateMembership(user.uuid, org.uuid, Instant.now()));
                return ctx.thenPersistAll(events,
                    () -> ctx.reply(cmd.membership));
            });
        return null;
    }
}
