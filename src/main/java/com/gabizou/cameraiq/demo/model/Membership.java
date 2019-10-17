package com.gabizou.cameraiq.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "organization_memberships")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false, updatable = false)
    private Organization organization;

    // Now we have the option of adding more metadata, such as roles, permissions
    // etc.and a role could be designed for organizational basis, with defaults
    // built in to the app, while customized roles can be created per organization


    public Membership(User user, Organization organization) {
        this.user = user;
        this.organization = organization;
    }
}
