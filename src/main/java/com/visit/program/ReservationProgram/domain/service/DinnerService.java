package com.visit.program.ReservationProgram.domain.service;

import com.visit.program.ReservationProgram.domain.dao.*;
import com.visit.program.ReservationProgram.domain.dto.DinnerInfoDTO;
import com.visit.program.ReservationProgram.domain.repository.DinnerRepository;
import com.visit.program.ReservationProgram.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DinnerService {

    private final DinnerRepository repository;

    @Transactional
    public Long save(DinnerReservationSave dinnerSave){
        DinnerReservation dinnerReservation = getDinnerReservation(dinnerSave);
        repository.saveReservation(dinnerReservation);
        log.info("savedId={}",dinnerReservation.getId());
        return dinnerReservation.getId();
    }

    private DinnerReservation getDinnerReservation(DinnerReservationSave save){
        String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));
        return new DinnerReservation(save.getLoginId(),save.getPart_name(),save.getEmployee_name(),save.getPhone_number(),save.getVisit_date()
        ,save.getContents(),save.getQty(),save.getPassword(),nowDate,null,false,null);
    }

    public List<DinnerReservationInfo> findAll(String visit_date){
        return repository.findAll(visit_date);
    }

    @Transactional
    public void saveInfo(SaveDinnerInfo dinnerInfo){
        repository.saveInfo(dinnerInfo);
    }

    @Transactional
    public void delete(Long reservation_info){
        DinnerInfo info = repository.findInfoOne(reservation_info);
        Long dinner_reservation_id = info.getDinner_reservation_id();
        repository.deleteDinnerInfo(reservation_info);
        repository.deleteDinnerReservation(dinner_reservation_id);
    }

    public DinnerReservation findOne(Long id){
        return repository.findOne(id);
    }

    @Transactional
    public void updateCheckedInfo(DinnerInfoCheckedUpdate update){
        repository.updateCheckedInfo(update);
    }

    @Transactional
    public void updateInfo(DinnerReservationUpdate dinnerReservationUpdate){
        repository.updateInfo(dinnerReservationUpdate);
    }
    @Transactional
    public void updateCheckedReservation(DinnerReservationCheckedUpdate update){
        repository.updateCheckedReservation(update);
    }

    public Long findByName(String employee_name){
        Long id = repository.findByName(employee_name);
        return id;
    }
    @Transactional
    public void updateAteInfo(Long id){
        repository.updateAteInfo(id);
    }


    public List<DinnerReservationInfo> findAllDTO(DinnerInfoDTO infoDTO){
        return repository.findAllDTO(infoDTO);
    };
}
