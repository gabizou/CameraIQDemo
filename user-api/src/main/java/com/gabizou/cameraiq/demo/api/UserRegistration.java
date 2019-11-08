package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.util.UUIDType5;
import com.google.common.base.MoreObjects;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("firstName", this.firstName)
            .add("lastName", this.lastName)
            .add("address", this.address)
            .add("email", this.email)
            .add("phoneNumber", this.phoneNumber)
            .toString();
    }

    /**
     * The entity ID is used for sharding UserRegistrations.
     * This allows implementing business-specific rules for
     * detecting and resolving conflicting registrations of users.
     *
     * The entity ID is effectively a primary key for a UserRegistration.
     * Two UserRegistrations are distinct if they have distinct entityIDs.
     *
     * @return
     */
    public String entityID() {
        return UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_OID, this.email).toString();
    }
}
