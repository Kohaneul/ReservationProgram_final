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
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Long save(DinnerReservationSave dinnerSave){
        DinnerReservation dinnerReservation = getDinnerReservation(dinnerSave);
        Long savedId = repository.saveReservation(dinnerReservation);
        log.info("savedId={}",savedId);
        return savedId;
    }

    private DinnerReservation getDinnerReservation(DinnerReservationSave save){
        String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy/MM/dd hh:mm"));
        return new DinnerReservation(save.getLoginId(),save.getPart_name(),save.getEmployee_name(),save.getPhone_number(),save.getVisit_date()
        ,save.getContents(),save.getQty(),save.getPassword(),nowDate,null,false,null);
    }

    public List<DinnerReservationInfo> findAll(String visit_date){
        return repository.findAll(visit_date);
    }

    @Transactional
    public Long saveInfo(DinnerInfo dinnerInfo){
      //  employee_id, dinner_reservation_id, is_checked
        Employee employee = employeeRepository.findById(dinnerInfo.getEmployee_id());
        Long saveInfoId = repository.saveInfo(dinnerInfo);
        return saveInfoId;
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


    public List<DinnerReservationInfo> findAllDTO(DinnerInfoDTO infoDTO){
        return repository.findAllDTO(infoDTO);
    };
}
