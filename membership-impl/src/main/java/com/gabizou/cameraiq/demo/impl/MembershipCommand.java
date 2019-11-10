package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationId;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserId;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import org.pcollections.POrderedSet;

import java.util.UUID;

/**
 * Defines all commands that {@link com.gabizou.cameraiq.demo.api.User}
 * and {@link com.gabizou.cameraiq.demo.api.Organization} support to
 * create {@link com.gabizou.cameraiq.demo.api.Membership Memberships}.
 */
public interface MembershipCommand extends Jsonable {

    @JsonDeserialize
    final class CreateMembership implements MembershipCommand,
        CompressedJsonable, PersistentEntity
            .ReplyType<Membership> {

        public final Membership membership;

        @JsonCreator
        public CreateMembership(final Membership membership) {
            this.membership = membership;
        }
    }

    @JsonDeserialize
    final class DeleteMembership implements MembershipCommand, CompressedJsonable, PersistentEntity.ReplyType<NotUsed> {

        public final OrganizationId orgId;
        public final UserId userId;

        @JsonCreator
        public DeleteMembership(OrganizationId orgId, UserId userId) {
            this.orgId = orgId;
            this.userId = userId;
        }
    }

    @JsonDeserialize
    public class GetMembersOfOrganization implements MembershipCommand,
        CompressedJsonable,
        PersistentEntity.ReplyType<POrderedSet<Membership>> {

        public final Organization organization;
        @JsonCreator
        public GetMembersOfOrganization(final Organization org) {
            this.organization = org;

        }
    }

    @JsonDeserialize
    public class GetMembershipsOfUser implements MembershipCommand,
    CompressedJsonable, PersistentEntity.ReplyType<POrderedSet<Membership>> {
        public final UserId userId;

        @JsonCreator
        public GetMembershipsOfUser(final UserId userId) {
            this.userId = userId;
        }
    }

    @JsonDeserialize
    public class GetMembership implements MembershipCommand,
        CompressedJsonable, PersistentEntity.ReplyType<Membership> {


        public final Organization org;
        public final UserId userId;

        @JsonCreator
        public GetMembership(final Organization org, final UserId request) {
            this.org = org;
            this.userId = request;
        }
    }

    public class RemoveAllMembershipsForUser implements MembershipCommand,
    CompressedJsonable, PersistentEntity.ReplyType<NotUsed> {
        public final UserId userId;

        public RemoveAllMembershipsForUser(final UserId userId) {
            this.userId = userId;
        }
    }
}
