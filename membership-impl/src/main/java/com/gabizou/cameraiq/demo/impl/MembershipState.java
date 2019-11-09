package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Membership;
import com.lightbend.lagom.serialization.Jsonable;
import org.pcollections.POrderedSet;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.util.Set;

@Immutable
@JsonDeserialize
public final class MembershipState implements Jsonable {

    public final POrderedSet<Membership> membership;

    @JsonCreator
    public MembershipState(final POrderedSet<Membership> membership) {
        this.membership = membership;
    }
}
