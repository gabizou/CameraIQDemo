package com.gabizou.cameraiq.demo;

import com.gabizou.cameraiq.demo.model.User;
import com.gabizou.cameraiq.demo.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Test
    public void testHibernateCreatesRecords() {
        List<User> users = service.list();

        Assert.assertEquals(users.size(), 3);
    }

}
