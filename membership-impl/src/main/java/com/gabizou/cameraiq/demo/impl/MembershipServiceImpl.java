package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.MembershipService;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.User;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PSequence;

public class MembershipServiceImpl implements MembershipService {

    private final PersistentEntityRegistry registry;

    @Inject
    public MembershipServiceImpl(final PersistentEntityRegistry registry) {
        this.registry = registry;
        registry.register(MembershipEntity.class);
    }

    @Override
    public ServiceCall<User, Organization> addMember(final String name) {
        return null;
    }

    @Override
    public ServiceCall<User, NotUsed> removeMember(final String name) {
        return null;
    }

    @Override
    public ServiceCall<NotUsed, PSequence<User>> getMembers(final String name) {
        return null;
    }

    @Override
    public ServiceCall<NotUsed, PSequence<Organization>> getOrganizations(final String id) {
        return null;
    }
}
