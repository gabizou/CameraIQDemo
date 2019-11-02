package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.User;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

public interface UserEvent extends Jsonable, AggregateEvent<UserEvent> {

    public static final AggregateEventTag<UserEvent> INSTANCE = AggregateEventTag.of(UserEvent.class);

    @Override
    default AggregateEventTagger<UserEvent> aggregateTag() {
        return UserEvent.INSTANCE;
    }

    /**
     * A simplified event marking the creation of a {@link User} within the system,
     * but barring any specific data of the user, only recording the {@link UUID} and
     * {@link Instant timestamp}. This event allows for us to facilitate easy user data
     * deletion requests without affecting the event journal.
     */
    @Immutable
    @JsonDeserialize
    public final class UserCreated implements UserEvent {

        public final UUID userId;
        public final Instant timestamp;

        public UserCreated(UUID userId, Instant timestamp) {
            this.userId = userId;
            this.timestamp = timestamp;
        }

        @JsonCreator
        private UserCreated(UUID uuid, Optional<Instant> timeStamp) {
            this(uuid, timeStamp.orElseGet(Instant::now));
        }


    }
}
