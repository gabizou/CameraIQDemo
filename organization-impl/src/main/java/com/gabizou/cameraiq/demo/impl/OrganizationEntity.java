package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.UserId;
import com.gabizou.cameraiq.demo.impl.events.OrganizationEvent;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.pcollections.OrderedPSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class OrganizationEntity extends PersistentEntity<OrganizationCommand,
    OrganizationEvent, OrganizationState> {

    @SuppressWarnings("unchecked")
    @Override
    public PersistentEntity.Behavior initialBehavior(final Optional<OrganizationState> snapshotState) {

        final BehaviorBuilder builder =
            this.newBehaviorBuilder(snapshotState.orElseGet(() -> new OrganizationState(OrderedPSet.empty())));
        builder.setCommandHandler(OrganizationCommand.CreateOrganization.class, (cmd, ctx) -> {
            final UserId orgId = new UserId(cmd.organization.orgId);
            if (this.state().organizations.contains(orgId)) {
                ctx.invalidCommand("Organization " + this.entityId() + " is " +
                    "already created");
                return ctx.done();
            }
            final ArrayList<OrganizationEvent> events = new ArrayList<>();
            // Add the organization created event
            events.add(new OrganizationEvent.OrganizationCreated(orgId, Instant.now()));
            return ctx.thenPersistAll(events, () -> ctx.reply(cmd.organization));
        });
        builder.setEventHandler(OrganizationEvent.OrganizationCreated.class, event ->
            new OrganizationState(this.state().organizations.plus(event.organizationId))
        );
        return builder.build();
    }
}
