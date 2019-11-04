package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Membership;
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


}
