package com.cgi.dentistapp.service;


import com.cgi.dentistapp.dto.DentistDTO;
import com.cgi.dentistapp.dto.DentistVisitDTO;
import com.cgi.dentistapp.entity.DentistEntity;
import com.cgi.dentistapp.entity.DentistVisitEntity;
import com.cgi.dentistapp.repository.DentistRepository;
import com.cgi.dentistapp.repository.DentistVisitRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DentistVisitService {

    private final int SESSION_LENGTH = 30;

    private final DentistRepository dentistRepo;

    private final DentistVisitRepository dentistVisitRepo;

    public void addNewDentist(String name) {
        DentistEntity dentistEntity = new DentistEntity();
        dentistEntity.setDentistName(name);
        dentistRepo.save(dentistEntity);
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
            System.out.println("An error has occurred during the saving.");
        }
    }

    public void updateDentistVisit(DentistVisitDTO dto) {
        try {
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
        } catch (Exception e) {
            System.out.println("An error has occurred during updating.");
        }
    }

    public Optional<DentistVisitDTO> getDentistVisitById(long id) {
        DentistVisitEntity visit = dentistVisitRepo.findOne(id);
        if (visit == null) return Optional.empty();
        return Optional.of(convertToDentistVisitDTO(visit));
    }

    public List<DentistVisitDTO> getAllDentistVisits() {
        return dentistVisitRepo.findAll().stream()
                .map(this::convertToDentistVisitDTO)
                .collect(Collectors.toList());
    }

    public List<DentistVisitDTO> getAllVisitsByDentistName(String name) {
        Optional<DentistEntity> dentistEntity = dentistRepo.findByDentistName(name);
        if (!dentistEntity.isPresent()) return new ArrayList<>();

        List<DentistVisitEntity> visits = dentistVisitRepo.findAll();
        visits = visits.stream().filter(x -> x.getDentist().equals(dentistEntity.get())).collect(Collectors.toList());

        return visits.stream()
                .map(this::convertToDentistVisitDTO)
                .collect(Collectors.toList());
    }

    public List<DentistVisitDTO> filterVisitsBySpecificDate(List<DentistVisitDTO> visits, Date date) {
        return visits.stream()
                .filter(x -> compareDates(x.getVisitDate(), date))
                .collect(Collectors.toList());
    }

    public List<DentistDTO> getAllDentists() {
        List<DentistEntity> dentists = dentistRepo.findAll();
        if (dentists == null) return new ArrayList<>();
        return dentists.stream().map(this::convertToDentistDTO).collect(Collectors.toList());
    }

    public Optional<DentistDTO> getDentistByName(String name) {
        Optional<DentistEntity> dentistEntity = dentistRepo.findByDentistName(name);
        return dentistEntity.map(this::convertToDentistDTO);
    }

    public void deleteDentistVisitById(long id) {
        dentistVisitRepo.delete(id);
    }

    // Assumed that each session lasts 30 minutes
    public boolean freeAtSpecificDateTime(String dentistName, Date date, String time) {
        List<DentistVisitDTO> allVisits = getAllVisitsByDentistName(dentistName);
        if (allVisits.isEmpty()) return true;
        List<DentistVisitDTO> visitsOnDate = filterVisitsBySpecificDate(allVisits, date);
        if (visitsOnDate.isEmpty()) return true;

        LocalTime start = LocalTime.parse(time);
        LocalTime end = start.plusMinutes(SESSION_LENGTH);

        List<DentistVisitDTO> visitsAtSpecificTime = visitsOnDate.stream()
                .filter(x -> timeNotTakenDuringVisit(x, start))
                .filter(x -> timeNotTakenDuringVisit(x, end))
                .collect(Collectors.toList());

        return visitsAtSpecificTime.isEmpty();
    }

    private boolean compareDates(Date date1, Date date2) {
        return date1.getTime() == date2.getTime();
    }

    private boolean timeNotTakenDuringVisit(DentistVisitDTO visit, LocalTime time) {
        LocalTime start = LocalTime.parse(visit.getVisitTime());
        LocalTime end = start.plusMinutes(SESSION_LENGTH);

        // check if the the time is between the start and the end
        return !time.isAfter(start) || !time.isBefore(end);
    }

    private DentistVisitDTO convertToDentistVisitDTO(DentistVisitEntity entity) {
        DentistVisitDTO dto = new DentistVisitDTO();

        dto.setId(entity.getId());
        dto.setDentistName(entity.getDentist().getDentistName());
        dto.setVisitDate(entity.getVisitDate());
        dto.setVisitTime(entity.getVisitTime());

        return dto;
    }

    private DentistDTO convertToDentistDTO(DentistEntity entity) {
        DentistDTO dto = new DentistDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getDentistName());
        return dto;
    }

}
