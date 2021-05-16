package com.udacity.jdnd.course3.critter.service;
import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.EmployeeSkill;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeDetailsRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.udacity.jdnd.course3.critter.entity.Pet;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeDetailsRepository employeeDetailsRepository;

    @Autowired
    PetRepository petRepository;

    public Optional<Customer> findCustomer(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer save(Customer c, List<Long> petIds) throws Exception {
        try{
                c.getPets().clear();
            for (Long petId : petIds) {
                Pet p = petRepository.findById(petId).get();
                c.getPets().add(p);
                }
                return customerRepository.save(c);
        } catch(Exception e) {
                throw new Exception ("Can not save customer: "+e);
        }
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> findEmployees(List<Long> employeeIds) throws Exception {
        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        if (employeeIds.size() != employees.size()) {
            List<Long> found = employees.stream().map(e -> e.getId()).collect(Collectors.toList());
            String missing = (String) employeeIds
                    .stream()
                    .filter( id -> !found.contains(id) )
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new Exception("No employee Id: " + missing);
        }
        return employees;
    }

    public List<Employee> findEmployeeByAvailablity(Set<EmployeeSkill> skills, LocalDate date) {

        List<Long> employeesIds = employeeDetailsRepository.findEmployeeIdBySkillsAndDay(skills, date.getDayOfWeek());
        List<Employee> employees = employeeRepository.findAllById(employeesIds);
        return employees;
    }

    @Transactional
    public Employee save(Employee e) {
        return employeeRepository.save(e);
    }

}
