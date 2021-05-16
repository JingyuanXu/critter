package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.EmployeeSkill;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class EmployeeDetailsRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Long> findEmployeeIdBySkillsAndDay(Set<EmployeeSkill> skillsSet, DayOfWeek dayOfWeek) {
        String sql = "select e.id " +
                "FROM Employee AS e, Employee_Skill AS es, Day_of_week AS d " +
                "WHERE e.id = es.id " +
                "AND e.id = d.id " +
                "AND es.skill in (" +
                skillsSet
                        .stream()
                        .map((skill) -> { return String.valueOf(skill.ordinal()); })
                        .collect(Collectors.joining(",")) +
                ") AND d.day = " + dayOfWeek.ordinal() + " " +
                "GROUP BY e.id HAVING count(es.skill) = " + skillsSet.size();

        Query selectQuery = entityManager.createNativeQuery(sql);

        List<BigInteger> result = selectQuery.getResultList();

        return result.stream()
                .map(BigInteger::longValue)
                .collect(Collectors.toList());

    }
}