package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Membership;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
@JsonDeserialize
public final class MembershipState implements Jsonable {

    public final Optional<Membership> membership;

    @JsonCreator
    public MembershipState(final Optional<Membership> membership) {
        this.membership = membership;
    }
}
