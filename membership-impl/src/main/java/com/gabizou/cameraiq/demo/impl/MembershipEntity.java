package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.impl.events.MembershipEvent;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.pcollections.OrderedPSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class MembershipEntity extends PersistentEntity<MembershipCommand,
    MembershipEvent, MembershipState> {
    @Override
    public Behavior initialBehavior(final Optional<MembershipState> snapshotState) {
        final BehaviorBuilder builder = this.newBehaviorBuilder(snapshotState.orElseGet(() -> new MembershipState(OrderedPSet.empty())));
        builder.setCommandHandler(MembershipCommand.CreateMembership.class,
            (cmd, ctx) -> {
                final User user = cmd.membership.user;
                final Organization org = cmd.membership.organization;
                final Membership membership = new Membership(org, user);
                if (this.state().membership.contains(membership)) {
                    final String message = String.format("Membership for %s " +
                        "in Organization %s already exists",
                        user.uuid, org.uuid);
                    ctx.invalidCommand(message);
                    return ctx.done();
                }
                final ArrayList<MembershipEvent> events = new ArrayList<>();
                events.add(new MembershipEvent.CreatedMembership(user, org, Instant.now()));
                return ctx.thenPersistAll(events,
                    () -> ctx.reply(cmd.membership));
            });
        builder.setEventHandler(MembershipEvent.CreatedMembership.class,
            event -> new MembershipState(this.state().membership.plus(new Membership(event.organizationId, event.userId))));
        builder.setCommandHandler(MembershipCommand.DeleteMembership.class, (cmd, ctx) -> {
            if (!this.state().membership.contains(cmd.deleted)) {
                ctx.invalidCommand("No membership exists for this user!");
                return ctx.done();
            }
            final ArrayList<MembershipEvent> events = new ArrayList<>();
            events.add(new MembershipEvent.MembershipDeleted(cmd.deleted, Instant.now()));
            return ctx.thenPersistAll(events, () -> ctx.reply(NotUsed.getInstance()));
        });
        builder.setEventHandler(MembershipEvent.MembershipDeleted.class,
            event -> new MembershipState(this.state().membership.minus(event.deleted)));
        return builder.build();
    }
}
