package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
@Immutable
public final class User {

    public final UUID uuid;
    public final String userId;
    public final String name;
    public final String email;

    public User(final UUID uuid, final String userId, final String name, final String email) {
        this.uuid = uuid;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.userId, user.userId) &&
                Objects.equals(this.name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.name);
    }
}
