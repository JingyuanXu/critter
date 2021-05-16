package com.udacity.jdnd.course3.critter.service;


import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PetService {

    @Autowired
    PetRepository petRepository;

    @Autowired
    CustomerRepository customerRepository;

    public Optional<Pet> findPetById(Long id) {
        return petRepository.findById(id);
    }

    public List<Pet> findAllPets() {
        return petRepository.findAll();
    }
    public List<Pet> findPetByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    public List<Pet> findPets(List<Long> petIds) throws Exception {
        List<Pet> pets = petRepository.findAllById(petIds);

        if (petIds.size() != pets.size()) {
            List<Long> found = pets.stream().map(p -> p.getId()).collect(Collectors.toList());
            String missing = (String) petIds
                    .stream()
                    .filter( id -> !found.contains(id) )
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new Exception("Can not find pets: " + missing);
        }
        return pets;
    }



    public Pet save(Pet p, Long ownerId) throws Exception {
        try{
            Customer owner = customerRepository.findById(ownerId).get();
            p.setOwner(owner);
            p.setOwner(owner);
            p = petRepository.save(p);
            owner.getPets().add(p);
            customerRepository.save(owner);
            return p;
        } catch (Exception e) {
            throw new Exception ("Can not save pets: "+e);
        }

    }
}
