package com.example.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.example.backend.model.Task;

@Repository
public interface TaskRepo extends JpaRepository<Task, Integer> {
    List<Task> findByUserId(Integer userId);
    List<Task> findAll();
}
