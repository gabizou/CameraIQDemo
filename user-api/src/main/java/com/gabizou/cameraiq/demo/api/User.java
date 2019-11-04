package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
@Immutable
public final class User {

    public final UUID uuid;
    public final UserRegistration info;

    public User(final UUID uuid, String firstName, String lastName,
                String address, String email, String phoneNumber ) {
        this.uuid = uuid;
        this.info = new UserRegistration(firstName, lastName, address, email,
            phoneNumber);
    }
    public User(final UUID uuid, final UserRegistration info) {
        this.uuid = uuid;
        this.info = info;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final User user = (User) o;
        return Objects.equals(this.uuid, user.uuid) &&
            Objects.equals(this.info, user.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid, this.info);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("uuid", this.uuid)
            .add("info", this.info)
            .toString();
    }
}
