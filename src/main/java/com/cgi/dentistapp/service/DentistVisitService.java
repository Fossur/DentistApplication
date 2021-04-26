package com.cgi.dentistapp.service;


import com.cgi.dentistapp.dto.DentistDTO;
import com.cgi.dentistapp.dto.DentistVisitDTO;
import com.cgi.dentistapp.entity.DentistEntity;
import com.cgi.dentistapp.entity.DentistVisitEntity;
import com.cgi.dentistapp.repository.DentistRepository;
import com.cgi.dentistapp.repository.DentistVisitRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class DentistVisitService {

    private final DentistRepository dentistRepo;

    private final DentistVisitRepository dentistVisitRepo;


    public DentistVisitService(DentistRepository dentistRepo, DentistVisitRepository dentistVisitRepo) {
        this.dentistRepo = dentistRepo;
        this.dentistVisitRepo = dentistVisitRepo;
    }

    public List<DentistDTO> getAllDentists() {
        List<DentistEntity> dentists = dentistRepo.findAll();
        if (dentists == null) return new ArrayList<>();
        return dentists.stream().map(this::convertToDentistDTO).collect(Collectors.toList());
    }

    private DentistDTO convertToDentistDTO(DentistEntity entity) {
        DentistDTO dto = new DentistDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getDentistName());
        return dto;
    }

    public void deleteDentistVisitById(long id) {
        dentistVisitRepo.delete(id);
    }

    public Optional<DentistVisitDTO> getDentistVisitById(long id) {
        DentistVisitEntity visit = dentistVisitRepo.findOne(id);
        if (visit == null) return Optional.empty();
        return Optional.of(convertToDentistVisitDTO(visit));
    }

    public Optional<DentistDTO> getDentistByName(String name) {
        Optional<DentistEntity> dentistEntity = dentistRepo.findByDentistName(name);
        if (!dentistEntity.isPresent()) return Optional.empty();
        return Optional.of(convertToDentistDTO(dentistEntity.get()));
    }

    public void changeDentistVisit(DentistVisitDTO dto) {
        DentistVisitEntity entity = dentistVisitRepo.findOne(dto.getId());

        Date date = dto.getVisitDate();
        if (date != null) entity.setVisitDate(date);

        String dentistName = dto.getDentistName();
        if (dentistName != null) {
            Optional<DentistEntity> dentist = dentistRepo.findByDentistName(dentistName);
            dentist.ifPresent(entity::setDentist);
        }

        String time = dto.getVisitTime();
        if (time != null) entity.setVisitTime(time);

        dentistVisitRepo.save(entity);
    }

    public void addNewDentist(String name) {
        DentistEntity dentistEntity = new DentistEntity();
        dentistEntity.setDentistName(name);
        dentistRepo.save(dentistEntity);
    }

    public List<DentistVisitEntity> getAllDentistVisitsByDentist(long id) {
        return dentistVisitRepo.findByDentistId(id);
    }

    private DentistVisitDTO convertToDentistVisitDTO(DentistVisitEntity entity) {
        DentistVisitDTO dto = new DentistVisitDTO();
        Optional<DentistEntity> dentistEntity = dentistRepo.findById(entity.getId());
        dto.setId(entity.getId());
        dto.setDentistName(dentistEntity.get().getDentistName());
        dto.setVisitDate(entity.getVisitDate());
        dto.setVisitTime(entity.getVisitTime());
        return dto;
    }

    public List<DentistVisitDTO> getAllDentistVisits() {
        return dentistVisitRepo.findAll().stream()
                .map(this::convertToDentistVisitDTO)
                .collect(Collectors.toList());
    }

    public List<DentistVisitDTO> getAllVisitsOnSpecificDate(Date date) {
        return dentistVisitRepo.findByVisitDate(date).stream()
                .map(this::convertToDentistVisitDTO)
                .collect(Collectors.toList());
    }

    public List<DentistVisitDTO> getAllVisitsByDentistName(String name) {
        Optional<DentistEntity> dentistEntity = dentistRepo.findByDentistName(name);
        if (!dentistEntity.isPresent()) return new ArrayList<>();
        List<DentistVisitEntity> visits = dentistVisitRepo.findByDentistId(dentistEntity.get().getId());
        return visits.stream()
                .map(this::convertToDentistVisitDTO)
                .collect(Collectors.toList());
    }

    public List<DentistVisitDTO> getAllVisitsByDentistNameOnSpecificDate(String name, Date date) {
        List<DentistVisitDTO> visits = getAllVisitsByDentistName(name);
        return visits.stream()
                .filter(x -> x.getVisitDate().equals(date))
                .collect(Collectors.toList());
    }

    public void addVisit(String dentistName, Date visitDate, String time) {
        try {
            Optional<DentistEntity> dentistEntity = dentistRepo.findByDentistName(dentistName);

            if (dentistEntity.isPresent()) {
                DentistVisitEntity newVisit = new DentistVisitEntity();
                newVisit.setDentist(dentistEntity.get());
                newVisit.setVisitDate(visitDate);
                newVisit.setVisitTime(time);
                dentistVisitRepo.save(newVisit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
