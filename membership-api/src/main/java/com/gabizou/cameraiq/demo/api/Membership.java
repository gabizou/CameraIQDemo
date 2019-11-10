package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.UUID;

@Immutable
@JsonDeserialize
public final class Membership {

    public final OrganizationId organization;
    public final UserId user;

    @JsonCreator
    public Membership(final OrganizationId organization, final UserId user) {
        this.organization = organization;
        this.user = user;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Membership that = (Membership) o;
        return Objects.equals(this.organization, that.organization) &&
            Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.organization, this.user);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("organization", this.organization)
            .add("user", this.user)
            .toString();
    }
}
