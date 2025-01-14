package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
@Immutable
public final class User {

    public final UserId userId;
    public final UserRegistration info;

    public User(final UserId userId, final UserRegistration info) {
        this.userId = userId;
        this.info = info;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final User user = (User) o;
        return Objects.equals(this.userId, user.userId) &&
            Objects.equals(this.info, user.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.info);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("uuid", this.userId)
            .add("info", this.info)
            .toString();
    }
}
