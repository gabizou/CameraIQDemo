package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.OrganizationId;
import com.gabizou.cameraiq.demo.api.UserId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class MembershipState implements Jsonable {

    public final ImmutableMultimap<UserId, Membership> userKeyedMemberships;
    public final ImmutableMultimap<OrganizationId, Membership> orgKeyedMemberships;

    @JsonCreator
    public MembershipState(final ImmutableMultimap<UserId, Membership> userKeyedMemberships,
                           final ImmutableMultimap<OrganizationId, Membership> orgKeyMemberships) {
        this.userKeyedMemberships = userKeyedMemberships;
        this.orgKeyedMemberships = orgKeyMemberships;
    }
}
