package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserId;
import com.lightbend.lagom.serialization.Jsonable;
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class UserState implements Jsonable {

    @JsonProperty
    public final OrderedPSet<UserId> users;

    @JsonCreator
    public UserState(POrderedSet<UserId> users) {
        this.users = OrderedPSet.from(users);
    }


}
