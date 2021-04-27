package com.cgi.dentistapp.repository;


import com.cgi.dentistapp.entity.DentistEntity;
import com.cgi.dentistapp.entity.DentistVisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface DentistVisitRepository extends JpaRepository<DentistVisitEntity, Long> {

    List<DentistVisitEntity> findByDentistId(Long dentistId);

    List<DentistVisitEntity> findByVisitDate(Date visitDate);

}
