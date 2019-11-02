package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a registration object for potential transformation
 * into a {@link User} with the provided field values:
 * <ul>
 *     <li>{@link #firstName First Name}</li>
 *     <li>{@link #lastName Last Name}</li>
 *     <li>{@link #email Email}</li>
 *     <li>{@link #address Address}</li>
 *     <li>{@link #phoneNumber Phone Number}</li>
 * </ul>
 */
@Immutable
@JsonDeserialize
public final class UserRegistration {

    public final String firstName;
    public final String lastName;
    public final String address;
    public final String email;
    public final String phoneNumber;

    @JsonCreator
    public UserRegistration(String firstName, String lastName, String address, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserRegistration that = (UserRegistration) o;
        return Objects.equals(this.firstName, that.firstName) &&
               Objects.equals(this.lastName, that.lastName) &&
               Objects.equals(this.address, that.address) &&
               Objects.equals(this.email, that.email) &&
               Objects.equals(this.phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.firstName, this.lastName, this.address, this.email, this.phoneNumber);
    }
}
