package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.MembershipService;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationService;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserService;
import com.gabizou.cameraiq.demo.util.UUIDType5;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import org.pcollections.PSequence;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class MembershipServiceImpl implements MembershipService {

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
    public ServiceCall<UUID, Organization> addMember(final String name) {
        return request -> {
            final CompletionStage<Organization> invoke = this.organizationService.organization(name).invoke();
            return invoke.thenApply(org -> {
                final UUID memberId = UUIDType5.nameUUIDFromNamespaceAndString(request, org.uuid.toString());
                this.users.lookupUser(request).invoke()
                    .thenApply(user -> {
                        final PersistentEntityRef<MembershipCommand> memberRef =
                            this.registry.refFor(MembershipEntity.class, memberId.toString());
                        final Membership membership = new Membership(org, user);
                        memberRef.ask(new MembershipCommand.CreateMembership(membership));
                        return org;
                    });
                return org;
            });
        };
    }

    @Override
    public ServiceCall<UUID, NotUsed> removeMember(final String name) {
        return request -> {
            final CompletionStage<Organization> org = this.organizationService.organization(name).invoke();
            return org.thenApply(organization -> {
                final UUID memberId = UUIDType5.nameUUIDFromNamespaceAndString(request, organization.uuid.toString());
                this.users.lookupUser(request).invoke()
                    .thenApply(user -> {
                        final PersistentEntityRef<MembershipCommand>
                            membership =
                            this.registry.refFor(MembershipEntity.class, memberId.toString());
                        final Membership toDelete = new Membership(organization, user);
                        membership.ask(new MembershipCommand.DeleteMembership(toDelete));
                        return org;
                    });
                return NotUsed.getInstance();
            });
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<User>> getMembers(final String name) {
        return notUsed -> {
            return this.users.getUsers().invoke().thenApply(users -> {
                return this.organizationService.getOrganizations().invoke().thenApply(orgs -> {
                    return users.parallelStream()
                        .map(userId -> {
                            return orgs.parallelStream()
                                .map(org -> {
                                    final UUID memberId = UUIDType5.nameUUIDFromNamespaceAndString(userId.uuid, org.uuid.toString());
                                    final PersistentEntityRef<MembershipCommand>
                                        membership =
                                        this.registry.refFor(MembershipEntity.class, memberId.toString());
                                    final Membership toDelete = new Membership(org, userId);
                                    membership.ask(new MembershipCommand.GetMembership(toDelete));
                                })
                        });

                });
            });
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<Organization>> getOrganizations(final String id) {
        return null;
    }

    @Override
    public ServiceCall<NotUsed, NotUsed> pruneAllMembershipsFor(UUID userId) {
        return notUsed -> this.users.lookupUser(userId).invoke().thenApply(user -> {

            return NotUsed.getInstance();
        });
    }
}

