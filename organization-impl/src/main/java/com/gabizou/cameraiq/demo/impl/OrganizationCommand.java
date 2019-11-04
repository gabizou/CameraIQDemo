package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.Organization;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * Defines all commands that {@link Organization} entities support.
 *
 * By convention, the commands are inner classes of this interface to see the
 * overall picture of what commands are supported, namely:
 * <ul>
 *     <li>Removing an organization</li>
 *     <li>Changing the organization name</li>
 *     <li>Changing the organization address</li>
 *     <li>Changing the organization phone number</li>
 * </ul>
 *
 * For membership management, that is handled in the {@code Membership-Impl}
 * along with querying for members, manipulating members, and removing members.
 */
public interface OrganizationCommand extends Jsonable {
    @JsonDeserialize
    final class CreateOrganization implements OrganizationCommand,
        CompressedJsonable, PersistentEntity.ReplyType<Organization> {

        public final Organization organization;

        @JsonCreator
        public CreateOrganization(final Organization organization) {
            this.organization = organization;
        }
    }
}
