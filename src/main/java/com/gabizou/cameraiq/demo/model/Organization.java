package com.gabizou.cameraiq.demo.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id", updatable = false, nullable = false)
    private long id;

    @NotBlank
    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @Column(name = "address", updatable = true, nullable = true)
    private String address;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "organization"
    )
    private Set<Membership> members = new HashSet<>();

    public Organization() {
    }

    public Organization(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Organization{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", address='" + address + '\'' +
               '}';
    }

    public long getId() {
        return id;
    }

    public Set<Membership> getMembers() {
        return members;
    }
}
