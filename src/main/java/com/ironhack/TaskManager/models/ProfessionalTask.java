package com.ironhack.TaskManager.models;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalTask extends Task{

    @Future
    private LocalDate dueDate;

    private String priority;
}
