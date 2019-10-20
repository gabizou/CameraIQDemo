package com.gabizou.cameraiq.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Represents a standard User object, this has the following properties that
 * are required to be usable:
 * <ul>
 *     <li>Unique ID represented by {@code long}</li>
 *     <li>Username represented by a String</li>
 *     <li>First name represented by a String</li>
 *     <li>Email name represented by a String</li>
 * </ul>
 * <p>
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
    @Column(name = "first_name", updatable = true, nullable = true)
    private String firstName;

    @Column(name = "last_name", updatable = true, nullable = true)
    private String lastName;

    // All Users must have an email
    @NotBlank
    @Column(name = "email", updatable = true, nullable = false)
    private String email;

    @Column(name = "address", updatable = true, nullable = true)
    private String address;

    @Column(name = "phone_number", updatable = true, nullable = true)
    private String phoneNumber;

    @JsonIgnoreProperties("handler")
    @OneToMany(mappedBy = "user")
    private Set<Membership> organizations = new HashSet<>();

    public User() {
    }

    public User(@NotBlank String username, String firstName, String lastName, @NotBlank String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String firstName, String lastName, String email, String address,
        String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the user first name.
     *
     * @return The user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the user last name.
     *
     * @return The user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the user's email, always available.
     *
     * @return The user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's address.
     *
     * @return The user's addresss
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the user's phone number.
     *
     * @return The user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's organization(s). If nothing is passed in, the
     * user is removed from all organizations.
     *
     * @param alpha The list of organizations
     */
    public void setOrganizations(Organization... alpha) {
        organizations.clear();
        for (Organization organization : alpha) {
            organizations.add(new Membership(this, organization));
        }
    }

    public void addMembership(Membership membership) {
        organizations.add(membership);
    }

    /**
     * Gets the uesr's username.
     *
     * @return The user's username
     */
    public String getUsername() {
        return this.username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
