package com.gabizou.cameraiq.demo.service;

import com.gabizou.cameraiq.demo.model.Organization;
import com.gabizou.cameraiq.demo.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository repo;

    public Organization addOrganization(Organization organization) {
        repo.save(organization);
        return organization;
    }

    public List<Organization> list() {
        return repo.findAll();
    }

}
