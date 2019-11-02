package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.User;
import com.lightbend.lagom.serialization.Jsonable;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class UserState implements Jsonable {

    public final Optional<User> user;

    @JsonCreator
    public UserState(Optional<User> user) {
        this.user = user;
    }


}
