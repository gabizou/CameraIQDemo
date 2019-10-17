package com.gabizou.cameraiq.demo.controllers;

import com.gabizou.cameraiq.demo.model.Organization;
import com.gabizou.cameraiq.demo.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/")
public class OrganizationController {

    final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd");

    @Autowired OrganizationService service;

    @RequestMapping(method = RequestMethod.GET)
    String home() {
        return "organization";
    }

    @RequestMapping(value = "organization", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    ModelAndView addOrganization(@RequestParam String name, @RequestParam String address) {
        final ModelAndView mv = new ModelAndView("organization");
        try {
            final Organization organization = new Organization();
            organization.setName(name);
            organization.setAddress(address);
            service.addOrganization(organization);
            mv.addObject("message", "Organization added with name: " + organization.getName());
        } catch (Exception e) {
            mv.addObject("message", "Failed to add organization: " + e.getLocalizedMessage());
        }
        mv.addObject("organizations", service.list());
        return mv;
    }
}
