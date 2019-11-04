package com.gabizou.cameraiq.demo.impl;

import com.gabizou.cameraiq.demo.api.MembershipService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.internal.javadsl.persistence.jdbc.JavadslJdbcOffsetStore;
import com.lightbend.lagom.internal.javadsl.persistence.jdbc.SlickProvider;
import com.lightbend.lagom.internal.persistence.jdbc.SlickOffsetStore;
import com.lightbend.lagom.javadsl.persistence.jdbc.GuiceSlickProvider;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class MembershipModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        this.bindService(MembershipService.class, MembershipServiceImpl.class);
        // JdbcPersistenceModule is disabled in application.conf to avoid conflicts with CassandraPersistenceModule.
        // We need to explicitly re-add the SlickOffsetStore binding that is required by the JpaPersistenceModule.
        this.bind(SlickProvider.class).toProvider(GuiceSlickProvider.class);
        this.bind(SlickOffsetStore.class).to(JavadslJdbcOffsetStore.class);
        // To use JdbcReadSide instead of JpaReadSide, uncomment these lines:
        // bind(JdbcReadSide.class).to(JdbcReadSideImpl.class);
        // bind(JdbcSession.class).to(JdbcSessionImpl.class);
    }
}
