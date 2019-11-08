package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.UserId;
import com.lightbend.lagom.serialization.Jsonable;
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class OrganizationState implements Jsonable {

    @JsonProperty
    public final OrderedPSet<UserId> organizations;

    @JsonCreator
    public OrganizationState(POrderedSet<UserId> organizations) {
        this.organizations = OrderedPSet.from(organizations);
    }
}
