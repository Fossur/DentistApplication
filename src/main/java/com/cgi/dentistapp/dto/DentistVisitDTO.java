package com.cgi.dentistapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DentistVisitDTO {

    Long id;

    @NotEmpty
    String dentistName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    Date visitDate;

    @NotEmpty
    @Size(min = 1, max = 50)
    String visitorName;

    @Email
    @NotEmpty
    String visitorEmail;

    @NotEmpty
    String visitStartTime;

    String visitEndTime;

}
