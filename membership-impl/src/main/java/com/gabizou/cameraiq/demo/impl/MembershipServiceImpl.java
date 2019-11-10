package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.MembershipService;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationService;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserId;
import com.gabizou.cameraiq.demo.api.UserService;
import com.gabizou.cameraiq.demo.util.DemoFunctional;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import org.pcollections.POrderedSet;

public class MembershipServiceImpl implements MembershipService {

    public static final String ENTITY_KEY = MembershipServiceImpl.class.getName();
    private final PersistentEntityRegistry registry;
    private final UserService users;
    private final OrganizationService organizationService;

    @Inject
    public MembershipServiceImpl(final PersistentEntityRegistry registry,
                                 final UserService users,
                                 final OrganizationService organizationService,
                                 final ReadSide readSide
    ) {
        this.registry = registry;
        registry.register(MembershipEntity.class);
        this.users = users;
        this.organizationService = organizationService;
        readSide.register(UserEventMembershipSideProcessor.class);
    }

    @Override
    public ServiceCall<UserId, Membership> addMember(final String name) {
        return request -> this.organizationService.organization(name)
            .invoke()
            .thenComposeAsync(org ->
                this.users.lookupUser(request.uuid)
                    .invoke()
                    .thenComposeAsync(user -> {
                        final Membership membership = new Membership(org.orgId,
                            user.userId);
                        return this.getMemberEntityFor().ask(new MembershipCommand.CreateMembership(membership));
                    })
            );
    }

    @Override
    public ServiceCall<UserId, Membership> getMembership(final String name) {
        return request -> this.organizationService.organization(name)
            .invoke()
            .thenComposeAsync(org -> this.getMemberEntityFor().ask(new MembershipCommand.GetMembership(org, request)));
    }

    @Override
    public ServiceCall<UserId, NotUsed> removeMember(final String name) {
        return request -> this.organizationService.organization(name)
            .invoke()
            .thenComposeAsync(organization ->
                this.users.lookupUser(request.uuid)
                    .invoke()
                    .thenComposeAsync(user -> this.getMemberEntityFor()
                        .ask(new MembershipCommand.DeleteMembership(organization.orgId, request))));

    }

    @Override
    public ServiceCall<NotUsed, POrderedSet<User>> getMembers(final String name) {
        return notUsed -> this.organizationService.organization(name)
            .invoke()
            .thenComposeAsync((Organization org) -> this.getMemberEntityFor()
                .ask(new MembershipCommand.GetMembersOfOrganization(org)))
            .thenApplyAsync(memberships -> memberships.stream()
                .map(membership -> this.users.lookupUser(membership.user.uuid))
                .map(ServiceCall::invoke)
                .map(stage -> stage.toCompletableFuture().join())
                .collect(DemoFunctional.toImmutableSet())
            );
    }

    @Override
    public ServiceCall<NotUsed, POrderedSet<Organization>> getOrganizations(final UserId id) {
        return notUsed ->
            this.getMemberEntityFor()
                .ask(new MembershipCommand.GetMembershipsOfUser(id));

    }

    @Override
    public ServiceCall<NotUsed, NotUsed> pruneAllMembershipsFor(UserId userId) {
        return notUsed -> this.getMemberEntityFor()
            .ask(new MembershipCommand.RemoveAllMembershipsForUser(userId));
    }

    private PersistentEntityRef<MembershipCommand> getMemberEntityFor() {
        return this.registry.refFor(MembershipEntity.class,
            MembershipServiceImpl.ENTITY_KEY);
    }
}

