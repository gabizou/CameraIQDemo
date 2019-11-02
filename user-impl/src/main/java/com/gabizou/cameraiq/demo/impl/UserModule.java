package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.UserService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class UserModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        this.bindService(UserService.class, UserServiceImpl.class);
    }
}
