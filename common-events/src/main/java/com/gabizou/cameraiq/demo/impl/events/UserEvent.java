package com.gabizou.cameraiq.demo.impl.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserId;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

public interface UserEvent extends Jsonable, AggregateEvent<UserEvent> {

    AggregateEventShards<UserEvent> INSTANCE = AggregateEventTag.sharded(UserEvent.class, 20);

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
    final class UserCreated implements UserEvent {

        @JsonProperty
        public final UserId userId;
        @JsonProperty
        public final Instant timestamp;

        public UserCreated(UserId userId, Instant timestamp) {
            this.userId = userId;
            this.timestamp = timestamp;
        }

        @JsonCreator
        private UserCreated(UserId uuid, Optional<Instant> timeStamp) {
            this(uuid, timeStamp.orElseGet(Instant::now));
        }
    }

    /**
     *
     */
    @Immutable
    @JsonDeserialize
    public class DeletedUser implements UserEvent {

        public final UserId userId;
        public final Instant timestamp;

        public DeletedUser(UserId userId, Instant timestamp) {
            this.userId = userId;
            this.timestamp = timestamp;
        }

        @JsonCreator
        private DeletedUser(UserId uuid, Optional<Instant> timeStamp) {
            this(uuid, timeStamp.orElseGet(Instant::now));
        }

    }
}
