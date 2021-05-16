package com.udacity.jdnd.course3.critter.dto;

import com.udacity.jdnd.course3.critter.entity.EmployeeSkill;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.Set;

/**
 * Represents the form that employee request and response data takes. Does not map
 * to the database directly.
 */
@Data
public class EmployeeDTO {
    private long id;
    private String name;
    private Set<EmployeeSkill> skills;
    private Set<DayOfWeek> daysAvailable;
}
