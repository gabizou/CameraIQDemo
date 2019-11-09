package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.MembershipService;
import com.gabizou.cameraiq.demo.api.OrganizationService;
import com.gabizou.cameraiq.demo.api.UserService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class MembershipModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        this.bindService(MembershipService.class, MembershipServiceImpl.class);
        this.bindClient(UserService.class);
        this.bindClient(OrganizationService.class);
    }
}
