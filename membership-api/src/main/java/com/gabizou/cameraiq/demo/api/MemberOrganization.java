package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class MemberOrganization {

    public final UUID organizationId;
    public final String name;

    @JsonCreator
    public MemberOrganization(UUID organizationId, String name) {
        this.organizationId = organizationId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MemberOrganization that = (MemberOrganization) o;
        return Objects.equals(this.organizationId, that.organizationId) &&
               Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.organizationId, this.name);
    }
}
