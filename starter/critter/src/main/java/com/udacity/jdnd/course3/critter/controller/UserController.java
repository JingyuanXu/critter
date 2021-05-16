package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final String []  PROPERTIES_TO_IGNORE_ON_COPY = { "id" };

    private UserService userService;

    private PetService petService;


    public UserController(UserService userService, PetService petService) {
        this.userService = userService;
        this.petService = petService;
    }

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) throws Exception {
        Long id = Optional.ofNullable(customerDTO.getId()).orElse(Long.valueOf(-1));
        Customer c = userService.findCustomer(id).orElseGet(Customer::new);
        BeanUtils.copyProperties(customerDTO, c, PROPERTIES_TO_IGNORE_ON_COPY);
        List<Long> petIds = Optional.ofNullable(customerDTO.getPetIds()).orElseGet(ArrayList::new);
        c = userService.save(c, petIds);
        return addCustomerDTO(c);
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = userService.getAllCustomers();
        return addCustomersDTOList(customers);
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId) throws Exception{
        Pet p = petService.findPetById(petId).orElseThrow(() -> new Exception("ID: " + petId));
        return addCustomerDTO(p.getOwner());
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) throws Exception {
        Employee e = userService.findEmployeeById(employeeDTO.getId()).orElseGet(Employee::new);
        BeanUtils.copyProperties(employeeDTO, e, PROPERTIES_TO_IGNORE_ON_COPY);
        e = userService.save(e);
        return addEmployeeDTO(e);
    }

    @GetMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) throws Exception {
        Employee e = userService.findEmployeeById(employeeId).orElseThrow(() -> new Exception("No Employee with ID: " + employeeId));
        return addEmployeeDTO(e);
    }

    @GetMapping("/employees")
    public List<EmployeeDTO> getEmployees() {
        List<Employee> employees = userService.findAllEmployees();
        return employees.stream().map((e) -> {return addEmployeeDTO(e);}).collect(Collectors.toList());
    }

    @Transactional
    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) throws Exception {
        Employee e = userService.findEmployeeById(employeeId).orElseThrow(() -> new Exception("NO available employee ID: " + employeeId));
        e.setDaysAvailable(daysAvailable);
        userService.save(e);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeRequestDTO) throws Exception {
        List<Employee> employees = userService.findEmployeeByAvailablity(employeeRequestDTO.getSkills(), employeeRequestDTO.getDate());
        return employees.stream().map(this::addEmployeeDTO).collect(Collectors.toList());
    }

    private EmployeeDTO addEmployeeDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        BeanUtils.copyProperties(employee, dto);
        return dto;
    }

    private CustomerDTO addCustomerDTO(Customer c){
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(c, dto);
        c.getPets().forEach( pet -> {
            dto.getPetIds().add(pet.getId());
        });
        return dto;
    }

    private List<CustomerDTO> addCustomersDTOList(List<Customer> customers) {
        List dtos = new ArrayList<CustomerDTO>();
        customers.forEach( c -> {
            dtos.add(this.addCustomerDTO((Customer)c));
        });
        return dtos;
    }

}