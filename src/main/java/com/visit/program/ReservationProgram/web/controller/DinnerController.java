package com.visit.program.ReservationProgram.web.controller;

import com.mysql.cj.Session;
import com.visit.program.ReservationProgram.domain.dao.*;
import com.visit.program.ReservationProgram.domain.dao.session.SessionConst;
import com.visit.program.ReservationProgram.domain.dto.DinnerInfoDTO;
import com.visit.program.ReservationProgram.domain.ex.ErrorMessage;
import com.visit.program.ReservationProgram.domain.service.DinnerService;
import com.visit.program.ReservationProgram.domain.service.EmployeeService;
import com.visit.program.ReservationProgram.web.controller.path.CutStr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/dinner/info")
@RequiredArgsConstructor
public class DinnerController {

    private final DinnerService service;
    private final EmployeeService employeeService;

    private String empName;
    private String startDate;
    private String endDate;

    @ModelAttribute(name="renewDate")
    public String renewDate(){
        return  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss"));
    }

//    @ModelAttribute(name="employees")
//    public List<Employee> employees(){
//        return employeeService.findAll();
//    }

    @GetMapping("/rapigen")
    public String enterPage(HttpSession session){
        session.setAttribute(SessionConst.DINNER_PROGRAM,"dinner");
        session.setAttribute(SessionConst.ACCESS_ID, UUID.randomUUID().toString().substring(0,8));
        log.info("session={}",session.getAttribute(SessionConst.DINNER_PROGRAM));
        return "redirect:/dinner/info/all";
    }



    @GetMapping("/all")
    public String save(@ModelAttribute("reservationDTO")DinnerInfoDTO dinnerInfoDTO, Model model, HttpSession session){
        List<DinnerReservationInfo> reservations = service.findAllDTO(dinnerInfoDTO);
        model.addAttribute("reservations",reservations);
        if(session.getAttribute(SessionConst.ADMIN_ID)!=null){
            return "view2/All2";
        }
        empName = dinnerInfoDTO.getEmployee_name();
        startDate=dinnerInfoDTO.getVisit_date1();
        endDate = dinnerInfoDTO.getVisit_date2();
        return "view2/All1";
    }


    @GetMapping("/save")
    public String save(@ModelAttribute("reservationSave")DinnerReservationSave reservationSave,Model model){
        LocalDateTime localDateTime = LocalDateTime.now();
        String hiddenValue = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        log.info("hiddenValue={}",hiddenValue);
        reservationSave.setVisit_date(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("hiddenValue",hiddenValue);
        return "view2/SaveForm";
    }

    /*
        @GetMapping("/save")
        public String save(@ModelAttribute("reservationSave")DinnerReservationSave reservationSave,Model model){
            LocalDateTime localDateTime = LocalDateTime.now();
            if(localDateTime.getDayOfWeek().toString().equals("FRIDAY")){
                localDateTime = localDateTime.plusDays(3);
        if(localDateTime.getHour()>=13){
        localDateTime = localDateTime.plusDays(1);
        }
            }

            else{
                if(localDateTime.getHour()<13){
                    localDateTime = localDateTime.plusDays(1);
                }
                else{
                    localDateTime = localDateTime.plusDays(2);
                }
            }
            String hiddenValue = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            log.info("hiddenValue={}",hiddenValue);
            reservationSave.setVisit_date(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("hiddenValue",hiddenValue);
         return "view2/SaveForm";
        }
    */
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("reservationSave")DinnerReservationSave reservationSave, BindingResult bindingResult){
//        LocalDateTime nowDate = LocalDateTime.now();
//        LocalDateTime reservationDate = LocalDateTime.parse(reservationSave.getVisit_date(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(bindingResult.hasErrors()){
            return "view2/SaveForm";
        }
        Long savedId = service.save(reservationSave);
        Employee employee = employeeService.findByLoginId(reservationSave.getLoginId());
        service.saveInfo(new SaveDinnerInfo(employee.getId(), savedId, false));
        return "redirect:/dinner/info/all";
    }

    @GetMapping("/update/check/{id}")
    public String checkUpdate(@PathVariable("id")Long id,HttpServletRequest request, HttpServletResponse response,Model model) throws IOException {
        DinnerReservation before = service.findOne(id);
        HttpSession session = request.getSession();
        if(session.getAttribute(SessionConst.ADMIN_ID)==null){
            if(before.getIs_checked()){
                CutStr.ex(ErrorMessage.ALREADY_CHECKED_MSG, request, response,1);
            }
        }
        DinnerReservationUpdate reservation = new DinnerReservationUpdate
                (before.getId(),before.getLoginId(),before.getEmployee_name(),before.getPhone_number(),before.getVisit_date(),before.getContents()
                        ,before.getQty());
        model.addAttribute("reservation",reservation);
        return "redirect:/dinner/info/update/{id}";
    }
    @GetMapping("/{id}")
    public String detailView(@PathVariable("id")Long id,Model model){
        DinnerReservation reservation = service.findOne(id);
        model.addAttribute("reservation",reservation);
        return "view2/ViewOne";
    }

    @PostMapping("/send")
    @ResponseBody
    public HashMap<String,Object> send(@RequestBody HashMap<String,Object> sendDTO){
        String loginId = (String) sendDTO.get("loginId");
        Employee employee = employeeService.findByLoginId(loginId);
        sendDTO.replace("phone_number",employee.getPhone_number());
        sendDTO.replace("employee_name",employee.getEmployee_name());
        sendDTO.replace("part_name",employee.getPart_name());

        return sendDTO;
    }

    @GetMapping("/update/{id}")
    public String updateView(@PathVariable("id")Long id, Model model,HttpServletRequest request){

        DinnerReservation before = service.findOne(id);
        DinnerReservationUpdate reservation = new DinnerReservationUpdate
                (before.getId(),before.getLoginId(),before.getEmployee_name(),before.getPhone_number(),before.getVisit_date(),before.getContents()
                        ,before.getQty());

        model.addAttribute("reservation",reservation);
        model.addAttribute("hiddenValue",reservation.getVisit_date());
        return "view2/UpdateForm";

    }

    @PostMapping("/update/{id}")
    public String updateView2(@PathVariable("id")Long id,@Valid @ModelAttribute("reservation")DinnerReservationUpdate reservation,BindingResult bindingResult){
        log.info("updateView method22222");
        if(bindingResult.hasErrors()){
            return "view2/UpdateForm";
        }
        service.updateInfo(reservation);
        log.info("id={} 수정성공",reservation.getLoginId());
        return  "redirect:/dinner/info/{id}";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id")Long id){
        log.info("삭제 로직");
        service.delete(id);
        log.info("삭제 완료 id={}",id);
        return "redirect:/dinner/info/all";
    }



    @RequestMapping("/download")
    public ResponseEntity<String> downLoadCSV(){
        log.info("empName={},startDate={},end={}",empName,startDate,endDate);
        HttpHeaders header = new HttpHeaders();
        LocalDateTime now = LocalDateTime.now();
        header.add("Content-Type","text/csv; charset=MS949");
        String fileName = "Dinner["+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"))+"]";
        header.add("Content-Disposition", "attachment;filename=\"" + fileName + ".csv\"");
        List<DinnerReservationInfo> reservations = service.findAllDTO(new DinnerInfoDTO(empName,startDate,endDate));
        return new ResponseEntity<>(content(reservations),header, HttpStatus.CREATED);
    }

    /*
        @RequestMapping("/download")
        public ResponseEntity<String> downLoadCSV(@ModelAttribute("reservationDTO")DinnerInfoDTO dinnerInfoDTO){
            HttpHeaders header = new HttpHeaders();
            header.add("Content-Type","text/csv; charset=MS949");
            String fileName = "Dinner["+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"))+"]";
            header.add("Content-Disposition", "attachment;filename=\"" + fileName + ".csv\"");
            List<DinnerReservationInfo> reservations = service.findAll();
            return new ResponseEntity<>(content(reservations),header, HttpStatus.CREATED);
        }
    */
    private String content(List<DinnerReservationInfo> employees){
        String data = "";
        data +="구분, no, 예약일, 인원, 소속 부서, 예약자, 등록 시간, 확인여부(미확인,확인)\n";
        for (int i = 0; i < employees.size(); i++) {
            data += "석식 예약"+",";
            data += (i+1)+ ",";
            data += employees.get(i).getVisit_date() + ",";
            data += employees.get(i).getQty() + ",";
            data += employees.get(i).getPart_name() + ",";
            data += employees.get(i).getEmployee_name() + ",";
            data += employees.get(i).getWrite_date() + ",";
            data += employees.get(i).getIs_checked() + "\n";
        }
        return data;
    }
    @GetMapping("/click/{id}")
    public String checkDinner(@PathVariable("id")Long id){
        DinnerReservation reservation = service.findOne(id);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss"));
        DinnerReservation dinnerReservation = service.findOne(id);
        service.updateCheckedReservation(new DinnerReservationCheckedUpdate(dinnerReservation.getId(), reservation.getIs_checked(), now));
        service.updateCheckedInfo(new DinnerInfoCheckedUpdate(id, dinnerReservation.getIs_checked()));
        log.info("완료 id={}",dinnerReservation.getIs_checked());
        return "redirect:/dinner/info/all";
    }


    @GetMapping("/admin/logOut")
    public String adminLogOut(HttpSession session){
        session.removeAttribute(SessionConst.ADMIN_ID);
        return "redirect:/dinner/info/all";
    }


}
