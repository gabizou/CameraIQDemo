package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationId;
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

    public static final String ENTITY_KEY = OrganizationServiceImpl.class.getName();
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
            final UUID uuid = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_OID, registrationInfo.name);
            final Organization newOrg = new Organization(new OrganizationId(uuid), registrationInfo);
            return this.repository.saveOrganization(newOrg)
                .thenComposeAsync(saved -> this.getEntityRef().ask(new OrganizationCommand.CreateOrganization(saved)));
        };
    }

    private PersistentEntityRef<OrganizationCommand> getEntityRef() {
        return this.registry.refFor(OrganizationEntity.class, OrganizationServiceImpl.ENTITY_KEY);
    }

    @Override
    public ServiceCall<NotUsed, POrderedSet<Organization>> getOrganizations() {
        return none -> this.repository.getOrganizations();
    }
}
