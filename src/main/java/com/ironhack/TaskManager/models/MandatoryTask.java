package com.ironhack.TaskManager.models;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MandatoryTask extends Task {

    @NotNull (message = "Start date must be creation task date")
    private LocalDate startDate;

    @NotNull @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @NotBlank(message = "Assigned user cannot be blank")
    private String assignedTo;

    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "Priority must be HIGH, MEDIUM, or LOW")
    @NotBlank(message = "Priority cannot be blank")
    private String priority;
}
