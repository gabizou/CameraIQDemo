package com.gabizou.cameraiq.demo;

import com.gabizou.cameraiq.demo.model.Organization;
import com.gabizou.cameraiq.demo.model.User;
import com.gabizou.cameraiq.demo.repositories.OrganizationRepository;
import com.gabizou.cameraiq.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppDemo implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    public static void main(String[] args) {
        SpringApplication.run(AppDemo.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();

        final User john = new User("John", "Smith", "john.smith@demo.com", null, null);
        final User jack = new User("Jack", "Wilson", "jack.wilson@demo.com", null, null);

        final Organization alpha = new Organization("Alpha Team", null);
        final Organization beta = new Organization("Beta Team", null);

        john.setOrganizations(alpha, beta);
        jack.setOrganizations(alpha);

        System.out.println("Donezo");
    }
}
