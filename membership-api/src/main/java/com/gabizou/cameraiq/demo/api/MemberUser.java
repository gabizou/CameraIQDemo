package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class MemberUser {

    public final UUID userId;
    public final String firstName;
    public final String lastName;

    @JsonCreator
    public MemberUser(UUID userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MemberUser that = (MemberUser) o;
        return Objects.equals(this.userId, that.userId) &&
               Objects.equals(this.firstName, that.firstName) &&
               Objects.equals(this.lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.firstName, this.lastName);
    }
}
