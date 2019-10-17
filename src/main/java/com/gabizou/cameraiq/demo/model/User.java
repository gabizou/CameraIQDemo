package com.gabizou.cameraiq.demo.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a standard User object, this has the following properties that
 * are required to be usable:
 * <ul>
 *     <li>Unique ID represented by {@code long}</li>
 *     <li>Username represented by a String</li>
 *     <li>First name represented by a String</li>
 *     <li>Email name represented by a String</li>
 * </ul>
 *
 * The following are nullable properties:
 * <ul>
 *     <li>User's Last Name represented as {@code String}</li>
 *     <li>User's address as {@code String}</li>
 *     <li>User's phone number as {@code String}</li>
 *     <li>Users's memberships to organizations. This membership will show
 *     their "roles" of the organization in a ModelView, and link to the
 *     Organization that the membership is a part of. This allows for
 *     flexibility of what a Membership consists of, since the membership can
 *     consist and expand to have the {@link Organization#getId()} and
 *     {@link User#id} as foreign keys. The extra metadata of the membership
 *     could be expanded on such as creation date, join date, team-only
 *     viewed status, role within organization, which can be a customized
 *     one, or an enumerated default option provided by the application.
 *     </li>
 * </ul>
 */
@Entity
@Table(name = "users")
public class User {

    // All users must have an id
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private long id;

    @NotBlank
    @Column(name = "username", updatable = true, nullable = false)
    private String username;

    // All users must have a name
    @NotNull
    @NotBlank
    @Column(name = "first_name", updatable = true, nullable = false)
    private String firstName;

    @Column(name = "last_name", updatable = true, nullable = true)
    private String lastName;

    // All Users must have an email
    @NotNull
    @NotBlank
    @Column(name = "email", updatable = true, nullable = false)
    private String email;

    @Column(name = "address", updatable = true, nullable = true)
    private String address;

    @Column(name = "phone_number", updatable = true, nullable = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    private Set<Membership> organizations = new HashSet<>();

    public User(String firstName, String lastName, String email, String address,
                String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setOrganizations(Organization... alpha) {
        organizations.clear();
        for (Organization organization : alpha) {
            organizations.add(new Membership(this, organization));
        }
    }

    public String getUsername() {
        return this.username;
    }
}
