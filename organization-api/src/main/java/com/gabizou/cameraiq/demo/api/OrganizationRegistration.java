package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class OrganizationRegistration {

    public final String name;
    public final String address;
    public final String phoneNumber;

    @JsonCreator
    public OrganizationRegistration(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
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
        OrganizationRegistration that = (OrganizationRegistration) o;
        return Objects.equals(this.name, that.name) &&
               Objects.equals(this.address, that.address) &&
               Objects.equals(this.phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.address, this.phoneNumber);
    }
}
