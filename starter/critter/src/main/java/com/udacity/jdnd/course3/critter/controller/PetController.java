package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.dto.PetDTO;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles web requests related to Pets.
 */


@RestController
@RequestMapping("/pet")
public class PetController {

    private static final String []  PROPERTIES_TO_IGNORE_ON_COPY = { "id" };

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    @PostMapping("/{ownerId}")
    public PetDTO updatePet(@PathVariable(name="ownerId") Long ownerId, @RequestBody PetDTO petDTO) throws Exception {
        petDTO.setOwnerId(ownerId);
        return savePet(petDTO);
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) throws Exception {
        long petId = Optional.ofNullable(petDTO.getId()).orElse(-1L);
        Pet p = petService.findPetById(Long.valueOf(petId)).orElseGet(Pet::new);
        BeanUtils.copyProperties(petDTO, p, PROPERTIES_TO_IGNORE_ON_COPY);
        p = petService.save(p, petDTO.getOwnerId());
        PetDTO dto = new PetDTO();
        BeanUtils.copyProperties(p, dto);
        dto.setOwnerId(p.getOwner().getId());
        return dto;
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) throws Exception {
        PetDTO dto = new PetDTO();
        Pet p = petService.findPetById(petId).orElseThrow(() -> new Exception("Can not find pet ID: " + petId));
        BeanUtils.copyProperties(p, dto);
        dto.setOwnerId(p.getOwner().getId());
        return dto;
    }

    @GetMapping
    public List<PetDTO> getPets(){
        List<Pet> pets = petService.findAllPets();
        return addPetsDTOList(pets);
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        List<Pet> pets = petService.findPetByOwner(Long.valueOf(ownerId));
        return addPetsDTOList(pets);
    }

    private List<PetDTO> addPetsDTOList(List<Pet> pets) {
        List<PetDTO> dtos = new ArrayList<>();
        pets.forEach(pet -> {
            dtos.add(new PetDTO(
                    pet.getId().longValue(),
                    pet.getType(),
                    pet.getName(),
                    pet.getOwner().getId().longValue(),
                    pet.getBirthDate(),
                    pet.getNotes()));
        });

        return dtos;
    }
}
