package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationRegistration;
import com.gabizou.cameraiq.demo.api.OrganizationService;
import com.gabizou.cameraiq.demo.util.UUIDType5;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcollections.POrderedSet;

import java.util.UUID;

public class OrganizationServiceImpl implements OrganizationService {

    private final PersistentEntityRegistry registry;
    private final OrganizationRepository repository;
    private static final Logger LOGGER = LogManager.getLogger(
        "OrganizationService");

    @Inject
    public OrganizationServiceImpl(final PersistentEntityRegistry registry, final OrganizationRepository repository) {
        this.registry = registry;
        registry.register(OrganizationEntity.class);
        this.repository = repository;
    }

    @Override
    public ServiceCall<NotUsed, Organization> organization(final String name) {
        return none -> {
            final UUID uuid = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_OID, name);
            return this.repository.lookupOrg(uuid);
        };
    }

    @Override
    public ServiceCall<OrganizationRegistration, Organization> createOrganization() {
        return registrationInfo -> {
            final PersistentEntityRef<OrganizationCommand> ref = this.registry.refFor(OrganizationEntity.class,
                registrationInfo.getEntityId());
            // Create a unique id based on the name, this will allow us to
            // completely block the creation and potential duplication of organization names previously registered
            // to where the uuid of the organization is now permanent, but the name can be changed.
            // Likewise, what this will allow us to do is record rename events to be able
            // to serve up legacy urls for organizations prior to a renaming.
            // Removing an organization of course still will remove those legacy bindings
            final UUID uuid = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_OID, registrationInfo.name);
            final Organization newOrg = new Organization(uuid, registrationInfo);
            OrganizationServiceImpl.LOGGER.info(String.format("Created new Organization{%s}", newOrg));
            // TODO - persistence of the organization
            return ref.ask(new OrganizationCommand.CreateOrganization(newOrg))
                .thenApply(this.repository::saveOrganization);
        };
    }

    @Override
    public ServiceCall<NotUsed, POrderedSet<Organization>> getOrganizations() {
        return none -> this.repository.getOrganizations();
    }
}
