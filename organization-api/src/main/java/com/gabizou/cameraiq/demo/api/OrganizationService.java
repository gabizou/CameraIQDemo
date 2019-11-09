package com.gabizou.cameraiq.demo.api;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import org.pcollections.POrderedSet;

import java.util.UUID;

public interface OrganizationService extends Service {

    /**
     * Gets an {@link Organization} by name.
     *
     * @param name The name of the organization
     * @return The organization
     */
    ServiceCall<NotUsed, Organization> organization(String name);

    /**
     * Creates a new {@link Organization} via {@link OrganizationRegistration registration object}
     * to facilitate unmodifiable and persistent infomration of the organization.
     * @return
     */
    ServiceCall<OrganizationRegistration, Organization> createOrganization();

    /**
     * Gets all registered {@link Organization Organizations}.
     *
     * @return The organizations
     */
    ServiceCall<NotUsed, POrderedSet<Organization>> getOrganizations();

    @Override
    default Descriptor descriptor() {
        return Service.named("organization")
            .withCalls(
                Service.pathCall("/api/organization/", this::createOrganization),
                Service.pathCall("/api/organization/:name", this::organization),
                Service.pathCall("/api/organization/", this::getOrganizations)
            ).withPathParamSerializer(UUID.class, PathParamSerializers.required("UUID", UUID::fromString, UUID::toString))
            .withAutoAcl(true);
    }

}