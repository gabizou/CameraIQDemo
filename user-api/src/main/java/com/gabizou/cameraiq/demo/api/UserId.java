package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.lightbend.lagom.serialization.Jsonable;

import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
public class UserId implements Jsonable {

    public final UUID userId;

    @JsonCreator
    public UserId(@JsonProperty("userId") final UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final UserId userId1 = (UserId) o;
        return Objects.equals(this.userId, userId1.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("userId", this.userId)
            .toString();
    }
}
