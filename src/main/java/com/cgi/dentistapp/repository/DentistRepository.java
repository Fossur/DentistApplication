package com.cgi.dentistapp.repository;


import com.cgi.dentistapp.entity.DentistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DentistRepository extends JpaRepository<DentistEntity, Long> {

    Optional<DentistEntity> findByDentistName(String dentistName);

    Optional<DentistEntity> findById(long id);

}
