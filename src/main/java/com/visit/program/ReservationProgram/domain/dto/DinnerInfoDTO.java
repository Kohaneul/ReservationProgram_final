package com.visit.program.ReservationProgram.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DinnerInfoDTO {
    private String employee_name;
    private String visit_date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


}
