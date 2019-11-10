package com.gabizou.cameraiq.demo.api;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;
import org.pcollections.PSequence;

import java.util.UUID;

public interface MembershipService extends Service {

    /**
     * Adds the provided {@link User user} to the {@link Organization
     * organization}
     * by {@code name}.
     *
     * @param organizationName The organization's name
     * @return The organization as a response
     */
    ServiceCall<UserId, Membership> addMember(String organizationName);

    /**
     * Gets the {@link Membership membership} of the {@link UserId user}
     * for the {@link Organization organization by name}.
     *
     * @param organizationName The organization's name
     * @return The membership as a response
     */
    ServiceCall<UserId, Membership> getMembership(final String organizationName);

    /**
     * Removes the requested {@link User User} from the queried
     * {@link Organization Organization}.
     *
     * @param organizationName The organization name
     */
    ServiceCall<UserId, NotUsed> removeMember(String organizationName);

    /**
     * Gets all {@link User users} part of an {@link Organization
     * organization}.
     *
     * @param organizationName The organization name
     * @return The sequence of users
     */
    ServiceCall<NotUsed, POrderedSet<User>> getMembers(String organizationName);

    /**
     * Gets the {@link PSequence list} of {@link Organization Organizations} that a
     * {@link UserId User} may belong to.
     *
     * @param id The user id
     * @return The sequence (list) of organizations
     */
    ServiceCall<NotUsed, POrderedSet<Organization>> getOrganizations(UserId id);

    /**
     * Prunes all memberships of the provided {@link UserId User id}.
     *
     * @param userId The user id
     * @return
     */
    ServiceCall<NotUsed, NotUsed> pruneAllMembershipsFor(UserId userId);

    @Override
    default Descriptor descriptor() {
        return Service.named("membership")
            .withCalls(
                Service.restCall(Method.GET, "/api/organization/:organizationName/members", this::getMembers),
                Service.restCall(Method.POST, "/api/organization/:organizationName/members", this::addMember),
                Service.restCall(Method.DELETE, "/api/organization/:organizationName/members", this::removeMember),
                Service.restCall(Method.GET, "/api/user/:id/memberships", this::getOrganizations),
                Service.restCall(Method.DELETE, "/api/user/:id/", this::pruneAllMembershipsFor),
                Service.restCall(Method.POST, "/api/organization/:organizationName/member", this::getMembership)
            )
            .withPathParamSerializer(UserId.class, PathParamSerializers.required("UserId", UserId::fromString, UserId::toString))
            .withPathParamSerializer(OrganizationId.class, PathParamSerializers.required("OrganizationId", OrganizationId::fromString, OrganizationId::toString))
            .withAutoAcl(true);
    }

}
