package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
@JsonDeserialize
public final class UserRegistration {

    public final String username;
    public final String email;
    public final String name;

    public UserRegistration(final String username, final String email, final String name) {
        this.username = username;
        this.email = email;
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final UserRegistration that = (UserRegistration) o;
        return Objects.equals(this.username, that.username) &&
            Objects.equals(this.email, that.email) &&
            Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.email, this.name);
    }
}
