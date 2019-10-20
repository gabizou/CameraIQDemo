package com.gabizou.cameraiq.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * Represents a "membership" of a {@link User} within
 * an {@link Organization}. Since an {@link Organization}
 * can have many {@link User users}, and a {@link User}
 * can be part of many {@link Organization organizations},
 * it stands that this membership ties the bridge of that
 * relationship.
 *
 * Future notes, the membership object can be expanded to
 * add other metadata features, such as roles, permissions,
 * etc. within an {@link Organization}, such that a {@link User}
 * can have multiple "roles" providing different access
 * to actions within an {@link Organization}.
 */
@Entity
@Table(name = "organization_memberships")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false, updatable = false)
    private Organization organization;

    // Now we have the option of adding more metadata, such as roles, permissions
    // etc.and a role could be designed for organizational basis, with defaults
    // built in to the app, while customized roles can be created per organization


    public Membership() {
    }

    public Membership(User user, Organization organization) {
        this.user = user;
        this.organization = organization;
    }

    /**
     * Gets the membership id used in the DB.
     *
     * @return The membership id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the {@link User} of this membership.
     *
     * @return The user of this membership
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the {@link Organization} of this membership.
     *
     * @return The organization of this membership
     */
    public Organization getOrganization() {
        return organization;
    }
}
