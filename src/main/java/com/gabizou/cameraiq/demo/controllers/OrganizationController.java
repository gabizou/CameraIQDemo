package com.gabizou.cameraiq.demo.controllers;

import com.gabizou.cameraiq.demo.model.Organization;
import com.gabizou.cameraiq.demo.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class OrganizationController {

    final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd");
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

    @PutMapping("/organizations/{id}/addUser/{employeeId}")
    public Organization addEmployee(@PathVariable("id") Long orgId, @PathVariable("employeeId") Long employeeId) {
        return service.addEmployee(orgId, employeeId);
    }

    @DeleteMapping("/organizations/{id}/delUser/{userId}")
    public Organization removeEmployee(@PathVariable("id") Long orgId, @PathVariable("userId") Long userId) {
        return service.removeEmployee(orgId, userId);
    }

}
