package com.gabizou.cameraiq.demo.impl.repo;

import com.gabizou.cameraiq.demo.api.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID",
        strategy = "org.hibernate.id" + ".UUIDGenerator")
    private UUID id;

    // All users must have a name
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // All Users must have an email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    public UserJpaEntity() {
    }

    public UserJpaEntity(User user) {
        this.id = user.uuid;
        this.firstName = user.info.firstName;
        this.lastName = user.info.lastName;
        this.email = user.info.email;
        this.address = user.info.address;
        this.phoneNumber = user.info.phoneNumber;
    }

    public UserJpaEntity(final String firstName,
                         final String lastName,
                         final String email,
                         final String address,
                         final String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() {
        return this.id;
    }


    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
