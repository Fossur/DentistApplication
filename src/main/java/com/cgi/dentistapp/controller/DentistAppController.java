package com.cgi.dentistapp.controller;


import com.cgi.dentistapp.dto.DentistDTO;
import com.cgi.dentistapp.dto.DentistVisitDTO;
import com.cgi.dentistapp.service.DentistVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
@EnableAutoConfiguration
@RequiredArgsConstructor
public class DentistAppController extends WebMvcConfigurerAdapter {

    private final DentistVisitService dentistVisitService;


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/registered").setViewName("registered");
        registry.addViewController("/updated").setViewName("updated");
    }

    @GetMapping
    public String showMain() {
        return "main";
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

    @GetMapping("/details")
    public String getDetails(@Valid DentistVisitDTO dentistVisitDTO, Model model) {
        List<DentistDTO> dentists = dentistVisitService.getAllDentists();

        model.addAttribute("dentists", dentists);

        return "details";
    }

    @PostMapping("/details")
    public String updateForm(@Valid DentistVisitDTO dentistVisitDTO,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !dentistVisitService.validateVisitInfo(dentistVisitDTO)) {
            return "details";
        }

        Date date = dentistVisitDTO.getVisitDate();
        String name = dentistVisitDTO.getDentistName();
        String time = dentistVisitDTO.getVisitStartTime();

        if (dentistVisitService.dentistOrTimeWasChanged(dentistVisitDTO, dentistVisitDTO.getId()) &&
            !dentistVisitService.freeAtSpecificDateTime(name, date, time)) {
            bindingResult.rejectValue("visitStartTime", "NotAvailable.dentistVisitDTO.visitTime");
            return "details";
        }

        dentistVisitService.updateDentistVisit(dentistVisitDTO);
        return "updated";
    }

    @GetMapping("/search")
    public String getVisits(DentistVisitDTO dentistVisitDTO, Model model) {
        return "search";
    }

    @PostMapping("/search")
    public String searchVisits(DentistVisitDTO dentistVisitDTO, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "search";
        }

        List<DentistVisitDTO> registrations = new ArrayList<>();
        String name = dentistVisitDTO.getDentistName();
        Date visitDate = dentistVisitDTO.getVisitDate();

        if (name != null) {
            if (name.equals("all")) registrations = dentistVisitService.getAllDentistVisits();
            else registrations = dentistVisitService.getAllVisitsByDentistName(name);
        }

        if (visitDate != null) registrations = dentistVisitService.filterVisitsBySpecificDate(registrations, visitDate);

        if (!registrations.isEmpty()) model.addAttribute("registrations", registrations);

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

        if (!dentistVisitService.validateVisitInfo(dentistVisitDTO)) return "visit";

        Date date = dentistVisitDTO.getVisitDate();
        String name = dentistVisitDTO.getDentistName();
        String time = dentistVisitDTO.getVisitStartTime();

        if (dentistVisitService.freeAtSpecificDateTime(name, date, time)) {
            dentistVisitService.addVisit(dentistVisitDTO);
            return "redirect:/registered";
        }
        
        // add error
        bindingResult.rejectValue("visitStartTime", "NotAvailable.dentistVisitDTO.visitTime");
        return "visit";

    }

}
