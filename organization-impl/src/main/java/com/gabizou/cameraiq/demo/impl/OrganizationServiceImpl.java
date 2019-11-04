package com.gabizou.cameraiq.demo.impl;

import akka.NotUsed;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationRegistration;
import com.gabizou.cameraiq.demo.api.OrganizationService;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.UUID;

public class OrganizationServiceImpl implements OrganizationService {

    private final PersistentEntityRegistry registry;
    private static final Logger LOGGER = LogManager.getLogger(
        "OrganizationService");

    @Inject
    public OrganizationServiceImpl(final PersistentEntityRegistry registry) {
        this.registry = registry;
        registry.register(OrganizationEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, Organization> organization(final String name) {
        return null;
    }

    @Override
    public ServiceCall<OrganizationRegistration, Organization> createOrganization() {
        return registrationInfo -> {
            final PersistentEntityRef<OrganizationCommand> ref = this.registry.refFor(OrganizationEntity.class,
                registrationInfo.getEntityId());
            final UUID uuid = UUID.randomUUID();
            final Organization newOrg = new Organization(uuid, registrationInfo);
            OrganizationServiceImpl.LOGGER.info(String.format("Created new Organization{%s}", newOrg));
            // TODO - persistence of the organization
            return ref.ask(new OrganizationCommand.CreateOrganization(newOrg));
        };
    }
}
