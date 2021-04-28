package com.cgi.dentistapp.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "dentist_visit")
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
    @NotEmpty
    private String visitStartTime;

    @NotNull
    @NotEmpty
    private String visitEndTime;

    @NotEmpty
    @Size(min = 1, max = 50)
    private String visitorName;

    @Email
    @NotNull
    private String visitorEmail;

}
