package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class Organization {

    public final UUID uuid;
    public final OrganizationRegistration info;

    public Organization(final UUID uuid, final OrganizationRegistration info) {
        this.uuid = uuid;
        this.info = info;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Organization that = (Organization) o;
        return Objects.equals(this.uuid, that.uuid) &&
            Objects.equals(this.info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid, this.info);
    }

}
