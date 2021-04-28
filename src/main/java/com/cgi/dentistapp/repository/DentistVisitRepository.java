package com.cgi.dentistapp.repository;


import com.cgi.dentistapp.entity.DentistVisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DentistVisitRepository extends JpaRepository<DentistVisitEntity, Long> { }
