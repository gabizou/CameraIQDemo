package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class Organization {

    public final OrganizationId orgId;
    public final OrganizationRegistration info;

    public Organization(final OrganizationId orgId, final OrganizationRegistration info) {
        this.orgId = orgId;
        this.info = info;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Organization that = (Organization) o;
        return Objects.equals(this.orgId, that.orgId) &&
            Objects.equals(this.info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.orgId, this.info);
    }

}
