package com.visit.program.ReservationProgram.domain.repository;

import com.visit.program.ReservationProgram.domain.dao.*;
import com.visit.program.ReservationProgram.domain.dto.DinnerInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface DinnerRepository {
    void saveReservation(DinnerReservation reservation);
    void updateInfo(DinnerReservationUpdate dinnerReservationUpdate);
    void updateCheckedInfo(DinnerInfoCheckedUpdate update);
    void updateCheckedReservation(DinnerReservationCheckedUpdate update);

    void saveInfo(SaveDinnerInfo dinnerInfo);
    List<DinnerReservationInfo> findAllDTO(DinnerInfoDTO infoDTO);

    List<DinnerReservationInfo> findAll(@Param("visit_date") String visit_date);

    DinnerReservation findOne(@Param("id")Long id);
    DinnerInfo findInfoOne(@Param("id")Long id);
    void deleteDinnerInfo(@Param("id")Long id);
    void deleteDinnerReservation(@Param("id")Long id);


}
