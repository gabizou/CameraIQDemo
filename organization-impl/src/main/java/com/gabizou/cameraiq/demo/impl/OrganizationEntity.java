package com.gabizou.cameraiq.demo.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class OrganizationEntity extends PersistentEntity<OrganizationCommand,
    OrganizationEvent, OrganizationState> {

    @Override
    public PersistentEntity.Behavior initialBehavior(final Optional<OrganizationState> snapshotState) {

        final BehaviorBuilder behaviorBuilder =
            this.newBehaviorBuilder(snapshotState.orElseGet(() -> new OrganizationState(Optional.empty())));
        behaviorBuilder.setCommandHandler(OrganizationCommand.CreateOrganization.class, (cmd, ctx) -> {
            if (this.state().organization.isPresent()) {
                ctx.invalidCommand("Organization " + this.entityId() + " is " +
                    "already created");
                return ctx.done();
            }
            final ArrayList<OrganizationEvent> events = new ArrayList<>();
            // Add the organization created event
            events.add(new OrganizationEvent.OrganizationCreated(cmd.organization.uuid, Instant.now()));
            return ctx.thenPersistAll(events, () -> ctx.reply(cmd.organization));
        });
        return behaviorBuilder.build();
    }
}
