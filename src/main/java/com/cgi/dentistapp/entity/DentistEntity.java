package com.cgi.dentistapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "dentist")
public class DentistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 1, max = 50)
    private String dentistName;


    private Integer sessionLength = 30;

    @OneToMany(mappedBy = "dentist")
    private List<DentistVisitEntity> visits;

}
