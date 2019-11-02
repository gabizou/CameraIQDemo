package com.gabizou.cameraiq.demo.api;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.PSequence;

public interface MembershipService extends Service {

    /**
     * Adds the provided {@link MemberUser user} to the {@link MemberOrganization organization}
     * by {@code name}.
     *
     * @param name The organization's name
     * @return The organization as a response
     */
    ServiceCall<MemberUser, MemberOrganization> addMember(String name);

    /**
     * Removes the requested {@link MemberUser User} from the queried
     * {@link MemberOrganization Organization}.
     *
     * @param name The organization name
     */
    ServiceCall<MemberUser, NotUsed> removeMember(String name);

    /**
     * Gets all {@link MemberUser users} part of an {@link MemberOrganization organization}.
     *
     * @param name The organization name
     * @return The sequence of users
     */
    ServiceCall<NotUsed, PSequence<MemberUser>> getMembers(String name);

    /**
     * Gets the {@link PSequence list} of {@link MemberOrganization Organizations} that a
     * {@link MemberUser User} may belong to.
     *
     * @param id The user id
     * @return The sequence (list) of organizations
     */
    ServiceCall<NotUsed, PSequence<MemberOrganization>> getOrganizations(String id);

    @Override
    default Descriptor descriptor() {
        return Service.named("membership")
            .withCalls(
                Service.pathCall("/api/organization/:name", this::getMembers),
                Service.restCall(Method.PUT, "/api/organization/:name", this::addMember),
                Service.restCall(Method.DELETE, "/api/organization/:name", this::removeMember),
                Service.pathCall("/api/user/:id", this::getOrganizations)
            )
            .withAutoAcl(true);
    }
}
