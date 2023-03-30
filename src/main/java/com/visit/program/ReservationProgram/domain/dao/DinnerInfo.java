package com.visit.program.ReservationProgram.domain.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DinnerInfo {
    private Long id;
    private Long employee_id;
    private Long dinner_reservation_id;
    private Boolean is_checked;


    public DinnerInfo(Long employee_id, Long dinner_reservation_id, Boolean is_checked) {
        this.employee_id = employee_id;
        this.dinner_reservation_id = dinner_reservation_id;
        this.is_checked = is_checked;
    }
}
