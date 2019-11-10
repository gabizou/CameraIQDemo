package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.UUID;

@Immutable
@JsonDeserialize
public class OrganizationId {

    public final UUID uuid;

    @JsonCreator
    public OrganizationId(final UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final OrganizationId that = (OrganizationId) o;
        return Objects.equals(this.uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("organizationId", this.uuid)
            .toString();
    }
}
