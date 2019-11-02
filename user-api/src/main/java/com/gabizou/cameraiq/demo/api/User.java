package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
@Immutable
public final class User {

    public final UUID uuid;
    public final String firstName;
    public final String lastName;
    public final String email;
    public final String phoneNumber;
    public final String address;

    public User(UUID uuid, String firstName, String lastName, String email, String phoneNumber, String address) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

}
