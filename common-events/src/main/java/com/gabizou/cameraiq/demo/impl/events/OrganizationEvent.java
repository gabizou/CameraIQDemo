package com.gabizou.cameraiq.demo.impl.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.UserId;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationEvent extends Jsonable,
    AggregateEvent<OrganizationEvent> {

    AggregateEventTag<OrganizationEvent> INSTANCE =
        AggregateEventTag.of(OrganizationEvent.class);

    @Override
    default AggregateEventTagger<OrganizationEvent> aggregateTag() {
        return OrganizationEvent.INSTANCE;
    }

    /**
     * A simplified event marking the creation of an
     * {@link com.gabizou.cameraiq.demo.api.Organization} within the system,
     * but barring any specific business data, only recording the {@link UUID}
     * and {@link Instant timestamp}. This event allows us to facilitate easy
     * organization removal without affecting the event journal.
     */
    @Immutable
    @JsonDeserialize
    final class OrganizationCreated implements OrganizationEvent {

        public final UserId organizationId;
        public final Instant timestamp;

        public OrganizationCreated(final UserId uuid, final Instant now) {
            this.organizationId = uuid;
            this.timestamp = now;
        }

        @JsonCreator
        private OrganizationCreated(final UserId uuid,
                                    Optional<Instant> timestamp) {
            this(uuid, timestamp.orElseGet(Instant::now));
        }
    }
}
