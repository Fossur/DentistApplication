package com.cgi.dentistapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DentistVisitDTO {

    Long id;

    @NotNull
    String dentistName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    Date visitDate;

    @NotEmpty
    String visitTime;

}
