package com.gabizou.cameraiq.demo.repositories;

import com.gabizou.cameraiq.demo.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

}
