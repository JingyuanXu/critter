package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.dto.ScheduleDTO;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private static final String []  PROPERTIES_TO_IGNORE_ON_COPY = { "id" };

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    UserService userService;

    @Autowired
    PetService petService;


    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) throws Exception{

        Schedule s = scheduleService.findSchedule(scheduleDTO.getId()).orElseGet(Schedule::new);
        s.setDate(scheduleDTO.getDate());
        s.setActivities(scheduleDTO.getActivities());
        s.setEmployees(userService.findEmployees(scheduleDTO.getEmployeeIds()));
        s.setPets(petService.findPets(scheduleDTO.getPetIds()));
        s = scheduleService.save(s);
        return addScheduleDTO(s);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleService.findAllSchedules();
        return addScheduleDTO(schedules);
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) throws Exception {
        return addScheduleDTO(scheduleService.findSchedulesForPet(petId));
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) throws Exception {
        return addScheduleDTO(scheduleService.findSchedulesForEmployee(employeeId));
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) throws Exception {
        return addScheduleDTO(scheduleService.findSchedulesForCustomer(customerId));
    }

    private List<ScheduleDTO> addScheduleDTO(List<Schedule> schedules) {
        return schedules
                .stream()
                .map(s -> { return addScheduleDTO(s); })
                .collect(Collectors.toList());
    }

    private ScheduleDTO addScheduleDTO(Schedule s) {
        ScheduleDTO dto = new ScheduleDTO();
        BeanUtils.copyProperties(s, dto);
        s.getEmployees().forEach(employee -> {dto.getEmployeeIds().add(employee.getId());});
        s.getPets().forEach(pet -> {dto.getPetIds().add(pet.getId());});
        return dto;
    }
}