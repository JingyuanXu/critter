package com.udacity.jdnd.course3.critter.service;
import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    UserService userService;

    @Autowired
    PetService petService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    CustomerRepository customerRepository;

    public Optional<Schedule> findSchedule(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> findAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> findSchedulesForPet(long petId) throws Exception {
        try{
            return petRepository.findById(petId).get().getSchedules();
        } catch(Exception e) {
            throw new Exception("Can not find Pet with ID: "+e);
        }

    }

    public List<Schedule> findSchedulesForEmployee(long employeeId) throws Exception {
        try{
            return employeeRepository.findById(employeeId).get().getSchedules();
        } catch (Exception e) {
            throw new Exception ("Can not find Employee with ID: "+e);
        }
    }

    public List<Schedule> findSchedulesForCustomer(long customerId) throws Exception {
        try{
            Customer c = customerRepository.findById(customerId).get();
            List<Schedule> customerSchedules = c.getPets()
                    .stream()
                    .map(Pet::getSchedules)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            return customerSchedules;
        } catch (Exception e){
            throw new Exception ("Can not schedule customer with ID: "+e);
        }
    }

    @Transactional
    public Schedule save(Schedule schedule) {
        schedule = scheduleRepository.save(schedule);
        for (Employee employee : schedule.getEmployees()){
            employee.getSchedules().add(schedule);
            employeeRepository.save(employee);
        }

        for (Pet pet : schedule.getPets()) {
            pet.getSchedules().add(schedule);
            petRepository.save(pet);
        }

        return schedule;
    }
}
