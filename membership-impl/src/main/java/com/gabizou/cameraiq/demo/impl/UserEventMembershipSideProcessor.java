package com.gabizou.cameraiq.demo.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.Attributes;
import akka.stream.javadsl.Flow;
import com.gabizou.cameraiq.demo.api.MembershipService;
import com.gabizou.cameraiq.demo.impl.events.UserEvent;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import org.pcollections.PSequence;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserEventMembershipSideProcessor extends ReadSideProcessor<UserEvent> {

    final MembershipService membership;

    @Inject
    public UserEventMembershipSideProcessor(MembershipService registry) {
        this.membership = registry;
    }


    @Override
    public ReadSideProcessor.ReadSideHandler<UserEvent> buildHandler() {
        return new ReadSideProcessor.ReadSideHandler<UserEvent>() {
            @Override
            public Flow<Pair<UserEvent, Offset>, Done, ?> handle() {
                return Flow.<Pair<UserEvent, Offset>>create()
                    .log("UserEvent")
                    .withAttributes(Attributes.createLogLevels(Attributes.logLevelInfo()))
                    .mapAsyncUnordered(10, e -> {
                        final UserEvent event = e.first();
                        if (event instanceof UserEvent.DeletedUser) {
                            final UUID userId = ((UserEvent.DeletedUser) event).userId.userId;
                            return UserEventMembershipSideProcessor.this.membership.pruneAllMembershipsFor(userId).invoke()
                                .thenApply(none -> Done.getInstance());
                        }
                        return CompletableFuture.completedFuture(Done.getInstance());
                    });
            }
        };
    }

    @Override
    public PSequence<AggregateEventTag<UserEvent>> aggregateTags() {
        return UserEvent.INSTANCE.allTags();
    }

}
