package com.ironhack.TaskManager.repositories;

import com.ironhack.TaskManager.models.PersonalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalTaskRepository extends JpaRepository<PersonalTask, Long> {



}
