package com.gabizou.cameraiq.demo.service;

import com.gabizou.cameraiq.demo.model.Membership;
import com.gabizou.cameraiq.demo.model.Organization;
import com.gabizou.cameraiq.demo.model.User;
import com.gabizou.cameraiq.demo.repositories.MembershipRepository;
import com.gabizou.cameraiq.demo.repositories.OrganizationRepository;
import com.gabizou.cameraiq.demo.repositories.UserRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Service
public class OrganizationService {

    @Autowired OrganizationRepository orgRepo;
    @Autowired UserRepository userRepo;
    @Autowired MembershipRepository memberRepo;
    @Autowired SessionFactory factory;

    @Transactional(readOnly = true)
    public List<Organization> getAll() {
        return orgRepo.findAll();
    }

    @Transactional
    public Organization addEmployee(@PathVariable("id") Long orgId, @PathVariable("employeeId") Long employeeId) {
        final Organization organiation = orgRepo.findById(orgId)
            .orElseThrow(() -> new EntityNotFoundException("No organization found by id: " + orgId));
        final User user = userRepo.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("No user found by id: " + employeeId));
        final Membership membership = new Membership(user, organiation);
        final List<Membership> existing = memberRepo.findAll(Example.of(membership, ExampleMatcher.matchingAll()));
        if (existing.isEmpty()) {
            user.addMembership(membership);
            memberRepo.save(membership);
            organiation.getMembers().add(membership);
            orgRepo.save(organiation);
            userRepo.save(user);
        }
        return organiation;
    }

    @Transactional
    public Organization one(@PathVariable long id) {
        return orgRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No entity found"));
    }

    @Transactional
    public Organization addNew(Organization organization) {
        return orgRepo.save(organization);
    }

    @Transactional
    public Organization modify(Long id, Organization organization) {
        final Organization
            org =
            orgRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("No organization found by id: " + id));
        org.setAddress(organization.getAddress());
        org.setName(organization.getName());
        final Organization updated = orgRepo.save(org);
        return updated;
    }

    @Transactional
    public Organization removeEmployee(long org, long user) {
        final User foundUser = userRepo.findById(user)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        final Organization organization = orgRepo.findById(org)
            .orElseThrow(() -> new EntityNotFoundException("Exception finding organization"));
        for (final Iterator<Membership> iter = organization.getMembers().iterator(); iter.hasNext(); ) {
            final Membership membership = iter.next();
            if (membership.getUser().equals(foundUser)) {
                memberRepo.delete(membership);
                iter.remove();
                break;
            }
        }
        orgRepo.save(organization);
        return organization;
    }


}
