package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.OrganizationService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class OrganizationModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        this.bind(OrganizationRepository.class).asEagerSingleton();
        this.bindService(OrganizationService.class, OrganizationServiceImpl.class);
    }
}
