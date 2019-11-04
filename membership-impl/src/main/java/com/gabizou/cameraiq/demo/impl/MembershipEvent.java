package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MembershipEvent extends Jsonable,
    AggregateEvent<MembershipEvent> {

    AggregateEventTag<MembershipEvent> INSTANCE =
        AggregateEventTag.of(MembershipEvent.class);

    @Override
    default AggregateEventTagger<MembershipEvent> aggregateTag() {
        return MembershipEvent.INSTANCE;
    }

    @Immutable
    @JsonDeserialize
    final class CreateMembership implements MembershipEvent {
        public final UUID userId;
        public final UUID organizationId;
        public final Instant timestamp;


        public CreateMembership(final UUID userId, final UUID organizationId, final Instant timestamp) {
            this.userId = userId;
            this.organizationId = organizationId;
            this.timestamp = timestamp;
        }

        @JsonCreator
        private CreateMembership(final UUID userId, final UUID organizationId, final Optional<Instant> timestamp) {
            this(userId, organizationId, timestamp.orElseGet(Instant::now));
        }
    }
}
