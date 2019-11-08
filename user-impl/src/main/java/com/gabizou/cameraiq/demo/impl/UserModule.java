package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.UserService;
import com.gabizou.cameraiq.demo.impl.repo.UserRepository;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class UserModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        this.bind(UserRepository.class).asEagerSingleton();
        this.bindService(UserService.class, UserServiceImpl.class);

    }
}
