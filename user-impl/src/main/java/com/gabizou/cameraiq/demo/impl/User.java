package com.gabizou.cameraiq.demo.impl;

import com.lightbend.lagom.serialization.Jsonable;

import java.util.Objects;
import java.util.UUID;


public final class User implements Jsonable {

    public final UUID id;
    public final String username;
    public final String email;
    public final String firstName;
    public final String lastName;
    public final String address;
    public final String phoneNumber;

    public User(final UUID id, final String username, final String email, final String firstName, final String lastName, final String address, final String phoneNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final User user = (User) o;
        return Objects.equals(this.id, user.id) &&
            Objects.equals(this.username, user.username) &&
            Objects.equals(this.email, user.email) &&
            Objects.equals(this.firstName, user.firstName) &&
            Objects.equals(this.lastName, user.lastName) &&
            Objects.equals(this.address, user.address) &&
            Objects.equals(this.phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username, this.email, this.firstName, this.lastName, this.address, this.phoneNumber);
    }
}
