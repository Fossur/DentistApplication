package com.cgi.dentistapp.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity
@Table(name = "dentist_visit")
@Getter
@Setter
@RequiredArgsConstructor
public class DentistVisitEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dentist_id")
    private DentistEntity dentist;

    @NotNull
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date visitDate;

    @NotNull
    private String visitTime;

}
