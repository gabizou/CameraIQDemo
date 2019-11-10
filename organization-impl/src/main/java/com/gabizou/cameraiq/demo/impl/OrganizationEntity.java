package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.OrganizationId;
import com.gabizou.cameraiq.demo.impl.events.OrganizationEvent;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcollections.OrderedPSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class OrganizationEntity extends PersistentEntity<OrganizationCommand,
    OrganizationEvent, OrganizationState> {

    private static final Logger LOGGER =
        LogManager.getLogger(OrganizationEntity.class);

    @SuppressWarnings("unchecked")
    @Override
    public PersistentEntity.Behavior initialBehavior(final Optional<OrganizationState> snapshotState) {

        final BehaviorBuilder builder =
            this.newBehaviorBuilder(snapshotState.orElseGet(() -> new OrganizationState(OrderedPSet.empty())));
        builder.setCommandHandler(OrganizationCommand.CreateOrganization.class, (cmd, ctx) -> {

            OrganizationEntity.LOGGER.debug("Received command: " + cmd);
            final OrganizationId orgId = cmd.organization.orgId;
            if (this.state().organizations.contains(orgId)) {
                OrganizationEntity.LOGGER.debug("No organization found by id: " + orgId);
                ctx.invalidCommand("Organization " + this.entityId() + " is " +
                    "already created");
                return ctx.done();
            }
            final ArrayList<OrganizationEvent> events = new ArrayList<>();
            // Add the organization created event
            OrganizationEntity.LOGGER.debug("Organization created to state: " + orgId);
            events.add(new OrganizationEvent.OrganizationCreated(orgId, Instant.now()));
            return ctx.thenPersistAll(events, () -> ctx.reply(cmd.organization));
        });
        builder.setEventHandler(OrganizationEvent.OrganizationCreated.class, event -> {
                OrganizationEntity.LOGGER.debug("Updating OrganizationState with (" + this.state().organizations.size() + ") organizations.");
                return new OrganizationState(this.state().organizations.plus(event.organizationId));
            }
        );
        return builder.build();
    }
}
