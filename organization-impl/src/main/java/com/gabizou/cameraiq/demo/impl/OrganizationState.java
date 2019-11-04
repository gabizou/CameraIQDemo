package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Organization;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
@JsonDeserialize
public final class OrganizationState implements Jsonable {

    public final Optional<Organization> organization;

    @JsonCreator
    public OrganizationState(final Optional<Organization> organization) {
        this.organization = organization;
    }
}
