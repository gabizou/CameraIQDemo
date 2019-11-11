package com.example.it;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.gabizou.cameraiq.demo.api.MembershipService;
import com.gabizou.cameraiq.demo.api.OrganizationService;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserRegistration;
import com.gabizou.cameraiq.demo.api.UserService;
import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class StreamIT {

    private static final String SERVICE_LOCATOR_URI = "http://localhost:9008";

    private static LagomClientFactory clientFactory;
    private static MembershipService membershipService;
    private static UserService userService;
    private static OrganizationService organizationService;
    private static ActorSystem system;
    private static Materializer mat;

    @BeforeClass
    public static void setup() {
        StreamIT.clientFactory = LagomClientFactory.create("integration-test", StreamIT.class.getClassLoader());
        // One of the clients can use the service locator, the other can use the service gateway, to test them both.
        StreamIT.userService = StreamIT.clientFactory.createDevClient(UserService.class, URI.create(StreamIT.SERVICE_LOCATOR_URI));
        StreamIT.organizationService = StreamIT.clientFactory.createDevClient(OrganizationService.class, URI.create(StreamIT.SERVICE_LOCATOR_URI));
        StreamIT.membershipService = StreamIT.clientFactory.createDevClient(MembershipService.class, URI.create(StreamIT.SERVICE_LOCATOR_URI));

        StreamIT.system = ActorSystem.create();
        StreamIT.mat = ActorMaterializer.create(StreamIT.system);
    }

    @Test
    public void createUser() throws Exception {
        final UserRegistration johnSmithInfo = new UserRegistration("John", "Smith", "123 Maple St.", "john.smith@example.com", "123-456-7890");
        final User answer = this.await(StreamIT.userService.createUser().invoke(johnSmithInfo));
        assertEquals(answer.info, johnSmithInfo);
        final User queried = this.await(StreamIT.userService.lookupUser(answer.userId).invoke());
        assertEquals(queried, answer);
    }


    private <T> T await(CompletionStage<T> future) throws Exception {
        return future.toCompletableFuture().get(10, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void tearDown() {
        if (StreamIT.clientFactory != null) {
            StreamIT.clientFactory.close();
        }
        if (StreamIT.system != null) {
            StreamIT.system.terminate();
        }
    }




}
