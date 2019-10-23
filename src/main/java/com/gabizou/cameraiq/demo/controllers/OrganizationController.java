package com.gabizou.cameraiq.demo.controllers;

import com.gabizou.cameraiq.demo.model.Organization;
import com.gabizou.cameraiq.demo.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OrganizationController {

    @Autowired OrganizationService service;

    @GetMapping("/organizations")
    List<Organization> home() {
        return service.getAll();
    }

    @PostMapping("/organizations")
    public Organization newOrganization(@RequestBody Organization organization) {
        return service.addNew(organization);
    }

    @GetMapping("/organizations/{id}")
    public Organization one(@PathVariable long id) {
        return service.one(id);
    }

    @PutMapping("/organizations/{id}")
    public Organization create(@PathVariable Long id, @Valid @RequestBody Organization organization) {
        return service.modify(id, organization);
    }

    @PutMapping("/organizations/{id}/addUser/{userId}")
    public Organization addEmployee(@PathVariable("id") Long orgId, @PathVariable("userId") Long employeeId) {
        return service.addEmployee(orgId, employeeId);
    }

    @DeleteMapping("/organizations/{id}/delUser/{userId}")
    public Organization removeEmployee(@PathVariable("id") Long orgId, @PathVariable("userId") Long userId) {
        return service.removeEmployee(orgId, userId);
    }

}
