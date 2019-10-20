package com.gabizou.cameraiq.demo;

import com.gabizou.cameraiq.demo.model.Membership;
import com.gabizou.cameraiq.demo.model.Organization;
import com.gabizou.cameraiq.demo.model.User;
import com.gabizou.cameraiq.demo.repositories.MembershipRepository;
import com.gabizou.cameraiq.demo.repositories.OrganizationRepository;
import com.gabizou.cameraiq.demo.repositories.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude =
    {DataSourceAutoConfiguration.class,
     DataSourceTransactionManagerAutoConfiguration.class,
     HibernateJpaAutoConfiguration.class
})
public class AppDemo implements CommandLineRunner {

    @Autowired Environment environment;
    @Autowired UserRepository userRepository;
    @Autowired OrganizationRepository organizationRepository;
    @Autowired MembershipRepository membershipRepository;
    @Autowired SessionFactory factory;

    public static void main(String[] args) {
        SpringApplication.run(AppDemo.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final User john = new User("jsmith", "John", "Smith", "john.smith@example.com");
        final User jack = new User("jwilson", "Jack", "Wilson", "jack.wilson@demo.com");

        final Organization alpha = new Organization("Alpha Team", null);
        final Organization beta = new Organization("Beta Team", null);

        final Membership johnAlpha = new Membership(john, alpha);
        final Membership johnBeta = new Membership(john, beta);


        final Membership membership = new Membership(jack, beta);

        Session session = null;
        Transaction transaction = null;
        try  {
            session = factory.openSession();
            transaction = session.beginTransaction();
            userRepository.save(john);
            userRepository.save(jack);
            transaction.commit();
            transaction = session.beginTransaction();
            john.addMembership(johnAlpha);
            john.addMembership(johnBeta);
            alpha.getMembers().add(johnAlpha);
            beta.getMembers().add(johnBeta);
            jack.addMembership(membership);
            beta.getMembers().add(membership);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();;
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        System.out.println("Donezo");
    }
}
