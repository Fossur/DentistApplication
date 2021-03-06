package com.cgi.dentistapp.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DentistDTO {

    Long id;

    @Size(min = 1, max = 50)
    String name;

    @Min(5)
    @Max(60)
    Integer sessionLength;

}
