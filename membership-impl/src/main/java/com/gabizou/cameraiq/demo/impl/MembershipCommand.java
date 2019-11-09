package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Membership;
import com.gabizou.cameraiq.demo.api.User;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

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

        public final Membership deleted;

        @JsonCreator
        public DeleteMembership(Membership deleted) {
            this.deleted = deleted;
        }
    }


    @JsonDeserialize
    public class GetMembership implements MembershipCommand, CompressedJsonable, PersistentEntity.ReplyType<User> {

        public final Membership membership;

        @JsonCreator
        public GetMembership(Membership membership) {

            this.membership = membership;
        }
    }
}
