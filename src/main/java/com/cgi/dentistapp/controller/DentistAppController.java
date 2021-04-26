package com.cgi.dentistapp.controller;

import com.cgi.dentistapp.dto.DentistDTO;
import com.cgi.dentistapp.dto.DentistVisitDTO;
import com.cgi.dentistapp.entity.DentistEntity;
import com.cgi.dentistapp.entity.DentistVisitEntity;
import com.cgi.dentistapp.service.DentistVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@EnableAutoConfiguration
public class DentistAppController extends WebMvcConfigurerAdapter {

    @Autowired
    private DentistVisitService dentistVisitService;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");

    }

    @GetMapping
    public String showMenu() {
        return "menu";
    }

    @ModelAttribute("dentists")
    public List<DentistDTO> getAllDentists() {
        return dentistVisitService.getAllDentists();
    }

    @PostMapping("/delete/{id}")
    public String deleteVisit(@PathVariable Long id) {
        dentistVisitService.deleteDentistVisitById(id);

        return "redirect:/search";
    }

    @GetMapping("/details/{id}")
    public String getDetails(@PathVariable Long id, DentistVisitDTO dentistVisitDTO, Model model) {
        Optional<DentistVisitDTO> visit = dentistVisitService.getDentistVisitById(id);
        if (!visit.isPresent()) return "redirect:/search";

        Optional<DentistDTO> dentist = dentistVisitService.getDentistByName(visit.get().getDentistName());
        if (!dentist.isPresent()) return "redirect:/search";

        List<DentistDTO> allDentists = dentistVisitService.getAllDentists();
        List<DentistDTO> otherDentists = allDentists.stream()
                .filter(x -> !x.getName().equals(visit.get().getDentistName()))
                .collect(Collectors.toList());

        model.addAttribute("dentists", otherDentists);
        model.addAttribute("visit", visit.get());

        return "details";
    }

    @PostMapping("/details")
    public String updateForm(DentistVisitDTO dentistVisitDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || dentistVisitDTO == null) {
            return "update";
        }

        dentistVisitService.changeDentistVisit(dentistVisitDTO);

        return "redirect:/details/" + dentistVisitDTO.getId();
    }

//    @PostMapping("/update/visit/{id}")
//    public String updateVisitForm(@PathVariable Long id, @ModelAttribute DentistVisitDTO dentistVisitDTO) {
//        if (id == null || dentistVisitDTO == null) return "form";
//        dentistVisitService.changeDentistVisit(id, dentistVisitDTO);
//        return "redirect:/search";
//    }

    @GetMapping("/search")
    public String getRegistrations(DentistVisitDTO dentistVisitDTO, Model model) {
        model.addAttribute("allRegistrations", dentistVisitService.getAllDentistVisits());

        return "search";
    }

    @PostMapping("/search")
    public String searchRegistrations(DentistVisitDTO dentistVisitDTO, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "search";
        }

        String registrations = "allRegistrations";
        String name = dentistVisitDTO.getDentistName();
        Date visitDate = dentistVisitDTO.getVisitDate();

        if (!name.equals("default") && visitDate != null) {
            model.addAttribute(registrations, dentistVisitService.getAllVisitsByDentistNameOnSpecificDate(name, visitDate));
        }
        else if (!name.equals("default")) {
            model.addAttribute(registrations, dentistVisitService.getAllVisitsByDentistName(name));
        }
        else if (visitDate != null) {
            model.addAttribute(registrations, dentistVisitService.getAllVisitsOnSpecificDate(visitDate));
        }
        else {
            model.addAttribute(registrations, dentistVisitService.getAllDentistVisits());
        }

        return "search";
    }

    @GetMapping("/dentist")
    public String showDentistForm(DentistDTO dentistDTO) {
        return "dentist";
    }

    @PostMapping("/dentist")
    public String postDentistForm(@Valid DentistDTO dentistDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "dentist";
        }

        if (dentistDTO.getName() == null || dentistDTO.getName().isEmpty()) return "dentist";

        dentistVisitService.addNewDentist(dentistDTO.getName());
        // TODO REDIRECT SUCCESS
        return "redirect:/registered";
    }


    @GetMapping("/visit")
    public String showVisitForm(DentistVisitDTO dentistVisitDTO) {
        return "visit";
    }

    @PostMapping("/visit")
    public String postVisitForm(@Valid DentistVisitDTO dentistVisitDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "visit";
        }

        if (dentistVisitDTO.getVisitDate() == null || dentistVisitDTO.getDentistName() == null || dentistVisitDTO.getVisitTime() == null) return "register";

        dentistVisitService.addVisit(dentistVisitDTO.getDentistName(), dentistVisitDTO.getVisitDate(), dentistVisitDTO.getVisitTime());
        // TODO REDIRECT SUCCESS
        return "redirect:/results";
    }

}
