package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.OrganizationId;
import com.gabizou.cameraiq.demo.api.UserId;
import com.gabizou.cameraiq.demo.impl.events.MembershipEvent;
import com.gabizou.cameraiq.demo.util.DemoFunctional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcollections.OrderedPSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class MembershipEntity extends PersistentEntity<MembershipCommand,
    MembershipEvent, MembershipState> {

    private static final Logger LOGGER = LogManager.getLogger(MembershipEntity.class);
    @SuppressWarnings("unchecked")
    @Override
    public Behavior initialBehavior(final Optional<MembershipState> snapshotState) {
        final BehaviorBuilder builder =
            this.newBehaviorBuilder(snapshotState.orElseGet(() -> new MembershipState(ImmutableListMultimap.of(), ImmutableListMultimap.of())));
        builder.setCommandHandler(MembershipCommand.CreateMembership.class,
            (cmd, ctx) -> {
                final UserId user = cmd.membership.user;
                final OrganizationId org = cmd.membership.organization;
                final Membership membership = new Membership(org, user);
                final ImmutableCollection<Membership> userMembership =
                    this.state().userKeyedMemberships.get(user);
                if (userMembership.contains(membership)) {
                    MembershipEntity.LOGGER.debug("Membership already created Membership" + "(user=" + user + ", org=" + org + ")");
                    final String message = String.format("Membership for %s " +
                        "in Organization %s already exists",
                        user, org);
                    ctx.invalidCommand(message);
                    return ctx.done();
                }
                final ImmutableCollection<Membership> orgMemberships = this.state().orgKeyedMemberships.get(org);
                if (orgMemberships.contains(membership)) {
                    final String message = String.format("Membership for %s in Organization %s " +
                        "already exists", user, org);
                    ctx.invalidCommand(message);
                    return ctx.done();
                }

                final ArrayList<MembershipEvent> events = new ArrayList<>();
                MembershipEntity.LOGGER.debug("Creating Membership" + "(user=" + user + ", " + "org=" + org + ")");
                events.add(new MembershipEvent.CretedMembership(user, org, Instant.now()));
                return ctx.thenPersistAll(events,
                    () -> ctx.reply(cmd.membership));
            });
        builder.setEventHandler(MembershipEvent.CretedMembership.class,
            event -> {
                final UserId userId = event.userId;
                final OrganizationId orgId = event.organizationId;
                final Membership membership = new Membership(orgId, userId);
                final ImmutableMultimap<OrganizationId, Membership> newOrgMemberships =
                    ImmutableMultimap.<OrganizationId, Membership>builder().putAll(this.state().orgKeyedMemberships)
                    .put(orgId, membership)
                    .build();
                final ImmutableMultimap<UserId, Membership> newUserMemberships =
                    ImmutableMultimap.<UserId, Membership>builder().putAll(this.state().userKeyedMemberships)
                        .put(userId, membership)
                        .build();
                MembershipEntity.LOGGER.debug("Updating Membership state with (" + newOrgMemberships.values().size() + ") memberships");
                return new MembershipState(newUserMemberships, newOrgMemberships);
            });
        builder.setCommandHandler(MembershipCommand.DeleteMembership.class, (cmd, ctx) -> {
            final ImmutableCollection<Membership> memberships =
                this.state().userKeyedMemberships.get(cmd.userId);
            if (memberships.isEmpty()) {
                ctx.invalidCommand("No memberships listed for the user: " + cmd.userId);
                return ctx.done();
            }
            for (final Membership membership : memberships) {
                if (cmd.orgId.equals(membership.organization)) {

                    final ArrayList<MembershipEvent> events = new ArrayList<>();
                    MembershipEntity.LOGGER.debug("Deleting Membership" + "(user=" + cmd.userId + ", org=" + cmd.orgId + ")");
                    events.add(new MembershipEvent.MembershipDeleted(membership, Instant.now()));
                    return ctx.thenPersistAll(events, () -> ctx.reply(NotUsed.getInstance()));
                }
            }
            return ctx.done();
        });
        builder.setEventHandler(MembershipEvent.MembershipDeleted.class,
            event -> {
                final MembershipState state = this.state();
                final ImmutableListMultimap.Builder<UserId,
                    Membership> userBuilder  =
                    ImmutableListMultimap.builder();
                final ImmutableListMultimap.Builder<OrganizationId,
                    Membership> orgBuilder  =
                    ImmutableListMultimap.builder();
                state.userKeyedMemberships.forEach((user, membership) -> {
                    if (!event.deleted.equals(membership)) {
                        userBuilder.put(user, membership);
                        orgBuilder.put(membership.organization, membership);
                    }
                });
                final ImmutableListMultimap<UserId, Membership> newUserMemberships = userBuilder.build();
                MembershipEntity.LOGGER.debug("Updating removed Membership state with (" + newUserMemberships.values().size() + ") memberships");
                return new MembershipState(newUserMemberships, orgBuilder.build());
            });
        builder.setReadOnlyCommandHandler(MembershipCommand.GetMembersOfOrganization.class, (cmd, ctx) -> {
            final ImmutableCollection<Membership> memberships = this.state().orgKeyedMemberships.get(cmd.organization.orgId);
            ctx.reply(OrderedPSet.from(memberships));
        });
        builder.setReadOnlyCommandHandler(MembershipCommand.GetMembershipsOfUser.class, (cmd, ctx) -> {
            final ImmutableCollection<Membership> memberships = this.state().userKeyedMemberships.get(cmd.userId);
            ctx.reply(memberships.stream()
                .collect(DemoFunctional.toImmutableSet())
            );
        });
        builder.setReadOnlyCommandHandler(MembershipCommand.GetMembership.class, (cmd, ctx) -> {
            final ImmutableCollection<Membership> memberships = this.state().userKeyedMemberships.get(cmd.userId);
            if (memberships.isEmpty()) {
                ctx.invalidCommand("No membership found for user " + cmd.userId + " in organiation " + cmd.org.info.name);
                return;
            }
            for (final Membership membership : memberships) {
                if (cmd.org.orgId.equals(membership.organization)) {
                    ctx.reply(membership);
                    return;
                }
            }
        });
        builder.setCommandHandler(MembershipCommand.RemoveAllMembershipsForUser.class, (cmd, ctx) -> {
            final ImmutableCollection<Membership> memberships = this.state().userKeyedMemberships.get(cmd.userId);
            if (memberships.isEmpty()) {
                // Basically, nothing else to do
                return ctx.done();
            }
            return ctx.thenPersistAll(memberships
                .stream()
                .map(membership -> new MembershipEvent.MembershipDeleted(membership, Instant.now()))
                .collect(Collectors.toList()), ctx::done);
        });
        return builder.build();
    }
}
