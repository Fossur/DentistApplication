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

    private final DentistRepository dentistRepo;

    private final DentistVisitRepository dentistVisitRepo;

    public void addNewDentist(String name) {
        DentistEntity dentistEntity = new DentistEntity();
        dentistEntity.setDentistName(name);
        dentistRepo.save(dentistEntity);
    }

    public void addVisit(DentistVisitDTO visitDTO) {
        try {
            Optional<DentistEntity> dentistEntity = dentistRepo.findByDentistName(visitDTO.getDentistName());

            if (dentistEntity.isPresent()) {
                DentistVisitEntity newVisit = new DentistVisitEntity();
                newVisit.setDentist(dentistEntity.get());
                newVisit.setVisitorEmail(visitDTO.getVisitorEmail());
                newVisit.setVisitorName(visitDTO.getVisitorName());

                newVisit.setVisitDate(visitDTO.getVisitDate());
                newVisit.setVisitStartTime(visitDTO.getVisitStartTime());

                // end time depends on dentists session length
                LocalTime timeObject = LocalTime.parse(visitDTO.getVisitStartTime());
                timeObject = timeObject.plusMinutes(dentistEntity.get().getSessionLength());

                newVisit.setVisitEndTime(timeObject.toString());

                dentistVisitRepo.save(newVisit);
            }
        } catch (Exception e) {
            System.out.println("An error has occurred during the saving.");
        }
    }

    public void updateDentistVisit(DentistVisitDTO dto) {
        try {
            DentistVisitEntity entity = dentistVisitRepo.findOne(dto.getId());

            // was changed in dto
            Date date = dto.getVisitDate();
            if (!datesEqual(date, entity.getVisitDate())) entity.setVisitDate(date);

            // was changed in dto
            String dentistName = dto.getDentistName();
            if (!dentistName.equals(entity.getDentist().getDentistName())) {
                Optional<DentistEntity> dentist = dentistRepo.findByDentistName(dentistName);
                dentist.ifPresent(entity::setDentist);


                // set new end time depending on dentist session length
                LocalTime endTime = LocalTime.parse(entity.getVisitStartTime());
                endTime = endTime.plusMinutes(entity.getDentist().getSessionLength());

                entity.setVisitEndTime(endTime.toString());
            }

            // if changed set new time and calculate new end time
            String startTime = dto.getVisitStartTime();
            if (!startTime.equals(entity.getVisitStartTime())) {
                entity.setVisitStartTime(startTime);

                LocalTime endTime = LocalTime.parse(startTime);
                endTime = endTime.plusMinutes(entity.getDentist().getSessionLength());

                entity.setVisitEndTime(endTime.toString());
            }


            String visitorName = dto.getVisitorName();
            if (!visitorName.equals(entity.getVisitorName())) entity.setVisitorName(visitorName);

            String visitorEmail = dto.getVisitorEmail();
            if (!visitorName.equals(entity.getVisitorEmail())) entity.setVisitorEmail(visitorEmail);

            dentistVisitRepo.save(entity);
        } catch (Exception e) {
            System.out.println("An error has occurred during updating.");
        }
    }

    public boolean dentistOrTimeWasChanged(DentistVisitDTO dto, long id) {
        DentistVisitEntity oldVisit = dentistVisitRepo.getOne(id);

        boolean sameDate = !datesEqual(dto.getVisitDate(), oldVisit.getVisitDate());
        boolean sameTime = dto.getVisitStartTime().equals(oldVisit.getVisitStartTime());
        boolean sameDentist = dto.getDentistName().equals(oldVisit.getDentist().getDentistName());
        return sameDate && sameTime && sameDentist;
    }

    public boolean validateVisitInfo(DentistVisitDTO dto) {
        Date visitDate = dto.getVisitDate();
        if (visitDate == null) return false;
        String visitStartTime = dto.getVisitStartTime();

        String dentistName = dto.getDentistName();
        String visitorEmail = dto.getVisitorEmail();
        String visitorName = dto.getVisitorName();

        return stringIsOK(visitStartTime) &&
                stringIsOK(dentistName) &&
                stringIsOK(visitorEmail) &&
                stringIsOK(visitorName);
    }

    private boolean stringIsOK(String string) {
        return string != null && !string.isEmpty();
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
                .filter(x -> datesEqual(x.getVisitDate(), date))
                .collect(Collectors.toList());
    }

    public List<DentistDTO> getAllDentists() {
        List<DentistEntity> dentists = dentistRepo.findAll();
        if (dentists == null) return new ArrayList<>();
        return dentists.stream().map(this::convertToDentistDTO).collect(Collectors.toList());
    }

    public void deleteDentistVisitById(long id) {
        dentistVisitRepo.delete(id);
    }

    public boolean freeAtSpecificDateTime(String dentistName, Date date, String time) {
        Optional<DentistEntity> dentistEntity = dentistRepo.findByDentistName(dentistName);
        if (!dentistEntity.isPresent()) return false;

        List<DentistVisitDTO> allVisits = dentistEntity.get().getVisits().stream()
                .map(this::convertToDentistVisitDTO)
                .collect(Collectors.toList());

        if (allVisits.isEmpty()) return true;
        List<DentistVisitDTO> visitsOnDate = filterVisitsBySpecificDate(allVisits, date);
        if (visitsOnDate.isEmpty()) return true;

        int sessionLength = dentistEntity.get().getSessionLength();

        LocalTime start = LocalTime.parse(time);
        LocalTime end = start.plusMinutes(sessionLength);

        List<DentistVisitDTO> visitsAtSpecificTime = visitsOnDate.stream()
                .filter(x -> timeTaken(x, start) || timeTaken(x, end))
                .collect(Collectors.toList());

        return visitsAtSpecificTime.isEmpty();
    }

    private boolean datesEqual(Date date1, Date date2) {
        return date1.getTime() == date2.getTime();
    }

    private boolean timeTaken(DentistVisitDTO visit, LocalTime time) {
        LocalTime start = LocalTime.parse(visit.getVisitStartTime());
        LocalTime end = LocalTime.parse(visit.getVisitEndTime());

        boolean equal = start.equals(time) || end.equals(time);
        boolean between = time.isAfter(start) && time.isBefore(end);

        return equal || between;
    }

    private DentistVisitDTO convertToDentistVisitDTO(DentistVisitEntity entity) {
        DentistVisitDTO dto = new DentistVisitDTO();

        dto.setDentistName(entity.getDentist().getDentistName());
        dto.setId(entity.getId());
        dto.setVisitDate(entity.getVisitDate());
        dto.setVisitStartTime(entity.getVisitStartTime());
        dto.setVisitEndTime(entity.getVisitEndTime());
        dto.setVisitorEmail(entity.getVisitorEmail());
        dto.setVisitorName(entity.getVisitorName());

        return dto;
    }

    private DentistDTO convertToDentistDTO(DentistEntity entity) {
        DentistDTO dto = new DentistDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getDentistName());
        dto.setSessionLength(entity.getSessionLength());

        return dto;
    }

}
