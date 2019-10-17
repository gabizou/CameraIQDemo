package com.gabizou.cameraiq.demo.repositories;

import com.gabizou.cameraiq.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
