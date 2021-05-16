package com.udacity.jdnd.course3.critter.entity;


import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=500)
    private String name;
}
