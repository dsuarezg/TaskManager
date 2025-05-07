package com.ironhack.TaskManager.models;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalTask extends Task{

    @NotBlank(message = "Place cannot be blank")
    private String place;

    @Min(value = 5, message = "Duration must be at least 5 minutes")
    private int duration;
}
