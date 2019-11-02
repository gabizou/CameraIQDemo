package com.gabizou.cameraiq.demo.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class Organization {

    public final UUID uuid;
    public final String name;
    public final String address;
    public final String phoneNumber;

    @JsonCreator
    public Organization(UUID uuid, String name, String address, String phoneNumber) {
        this.uuid = uuid;
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
        Organization that = (Organization) o;
        return Objects.equals(this.uuid, that.uuid) &&
               Objects.equals(this.name, that.name) &&
               Objects.equals(this.address, that.address) &&
               Objects.equals(this.phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid, this.name, this.address, this.phoneNumber);
    }
}
