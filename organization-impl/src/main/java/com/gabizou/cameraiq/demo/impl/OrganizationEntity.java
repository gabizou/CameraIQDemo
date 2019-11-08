package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.UserId;
import com.gabizou.cameraiq.demo.impl.events.OrganizationEvent;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.pcollections.OrderedPSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class OrganizationEntity extends PersistentEntity<OrganizationCommand,
    OrganizationEvent, OrganizationState> {

    @Override
    public PersistentEntity.Behavior initialBehavior(final Optional<OrganizationState> snapshotState) {

        final BehaviorBuilder behaviorBuilder =
            this.newBehaviorBuilder(snapshotState.orElseGet(() -> new OrganizationState(OrderedPSet.empty())));
        behaviorBuilder.setCommandHandler(OrganizationCommand.CreateOrganization.class, (cmd, ctx) -> {
            final UserId userId = new UserId(cmd.organization.uuid);
            if (this.state().organizations.contains(userId)) {
                ctx.invalidCommand("Organization " + this.entityId() + " is " +
                    "already created");
                return ctx.done();
            }
            final ArrayList<OrganizationEvent> events = new ArrayList<>();
            // Add the organization created event
            events.add(new OrganizationEvent.OrganizationCreated(userId, Instant.now()));
            return ctx.thenPersistAll(events, () -> ctx.reply(cmd.organization));
        });
        behaviorBuilder.setEventHandler(OrganizationEvent.OrganizationCreated.class, event ->
            new OrganizationState(this.state().organizations.plus(event.organizationId))
        );
        return behaviorBuilder.build();
    }
}
