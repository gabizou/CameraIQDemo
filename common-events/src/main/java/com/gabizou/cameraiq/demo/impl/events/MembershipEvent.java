package com.gabizou.cameraiq.demo.impl.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationId;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserId;
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
    final class CretedMembership implements MembershipEvent {
        public final UserId userId;
        public final OrganizationId organizationId;
        public final Instant timestamp;


        public CretedMembership(final UserId userId, final OrganizationId organizationId, final Instant timestamp) {
            this.userId = userId;
            this.organizationId = organizationId;
            this.timestamp = timestamp;
        }

        @JsonCreator
        private CretedMembership(final UserId userId, final OrganizationId organizationId, final Optional<Instant> timestamp) {
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
