package com.cgi.dentistapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DentistVisitDTO {

    Long id;

    @Size(min = 1, max = 50)
    String dentistName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date visitDate;

    String visitTime;

}
