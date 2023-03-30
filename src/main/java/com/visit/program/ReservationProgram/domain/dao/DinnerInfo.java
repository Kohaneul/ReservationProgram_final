package com.visit.program.ReservationProgram.domain.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DinnerInfo {
    private Long id;
    private Long employee_id;
    private Long dinner_reservation_id;
    private Boolean is_checked;
}
