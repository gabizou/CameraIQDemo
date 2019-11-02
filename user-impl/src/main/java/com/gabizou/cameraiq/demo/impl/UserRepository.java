package com.gabizou.cameraiq.demo.impl;

import akka.Done;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class UserRepository {

    private final CassandraSession uninitialisedSession;
    // Will return the session when the Cassandra tables have been successfully created
    private volatile CompletableFuture<com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession> initialisedSession;

    @Inject
    public UserRepository(final CassandraSession session, ReadSide readSide) {
        this.uninitialisedSession = session;
        readSide.register(UserEventProcessor.class);
        this.session();
    }
    private CompletionStage<com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession> session() {
        // If there's no initialised session, or if the initialised session future completed
        // with an exception, then reinitialise the session and attempt to create the tables
        if (this.initialisedSession == null || this.initialisedSession.isCompletedExceptionally()) {
            this.initialisedSession = this.uninitialisedSession.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS greeting_message (name text PRIMARY KEY, message text)"
            ).thenApply(done -> this.uninitialisedSession).toCompletableFuture();
        }
        return this.initialisedSession;
    }



    private static class UserEventProcessor extends ReadSideProcessor<UserEvent> {

        @Override
        public ReadSideHandler<UserEvent> buildHandler() {
            return null;
        }

        @Override
        public PSequence<AggregateEventTag<UserEvent>> aggregateTags() {
            return null;
        }
    }
}
