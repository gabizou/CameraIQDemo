package com.gabizou.cameraiq.demo.impl.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.User;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Optional;

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
    final class CreatedMembership implements MembershipEvent {
        public final User userId;
        public final Organization organizationId;
        public final Instant timestamp;


        public CreatedMembership(final User userId, final Organization organizationId, final Instant timestamp) {
            this.userId = userId;
            this.organizationId = organizationId;
            this.timestamp = timestamp;
        }

        @JsonCreator
        private CreatedMembership(final User userId, final Organization organizationId, final Optional<Instant> timestamp) {
            this(userId, organizationId, timestamp.orElseGet(Instant::now));
        }
    }

    @JsonDeserialize
    public class MembershipDeleted implements MembershipEvent {

        public final Membership deleted;
        public final Instant timestamp;

        @JsonCreator
        public MembershipDeleted(Membership deleted, Instant now) {
            this.deleted = deleted;
            this.timestamp = now;
        }
    }
}
