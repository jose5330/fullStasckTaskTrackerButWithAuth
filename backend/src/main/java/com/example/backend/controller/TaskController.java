package com.example.backend.controller;

import com.example.backend.model.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import com.example.backend.repository.TaskRepo;
import com.example.backend.model.Task;
import jakarta.servlet.http.HttpServletRequest;

@RestController()
@EnableMethodSecurity
@RequestMapping("/api")
public class TaskController {
    @Autowired
    private TaskRepo taskRepo;

    @GetMapping("/user/tasks")
    public List<Task> getTasks(HttpServletRequest request) throws Exception {
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal != null) {
            return taskRepo.findByUserId(userPrincipal.getId());
        }
        throw new Exception("User not authenticated");
    }

    @GetMapping("/admin/tasks")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Task> getAllTasks(HttpServletRequest request) throws Exception {
        return taskRepo.findAll();
    }

    @DeleteMapping("/admin/tasks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void removeTask(@PathVariable Integer id) throws Exception {
        Optional<Task> optional = taskRepo.findById(id);
        if (optional.isPresent()) {
            taskRepo.deleteById(id);
        } else {
            throw new Exception("Task not found");
        }
    }
    @PutMapping("/admin/tasks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Task updateTask(@PathVariable Integer id,@RequestBody Task task) throws Exception {
        Optional<Task> optional = taskRepo.findById(id);
        if (optional.isPresent()) 
        {
            Task existingTask = optional.get();
            existingTask.setUserId(task.getUserId());
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setCompleted(task.isCompleted() || existingTask.isCompleted());
            return taskRepo.save(existingTask);
        } else {
            throw new Exception("Task not found");
        }
    }

    @PostMapping("/admin/tasks")
    @PreAuthorize("hasRole('ADMIN')")
    public Task createTask(@RequestBody Task task) {
        return taskRepo.save(task);
    }

    @PutMapping("/user/tasks/{id}")
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    public void completeTask(@PathVariable Integer id) throws Exception {
        Optional<Task> optional = taskRepo.findById(id);
        if (optional.isPresent()) 
        {
            Task existingTask = optional.get();
            existingTask.setCompleted(!existingTask.isCompleted());
            taskRepo.save(existingTask);
        } else {
            throw new Exception("Task not found");
        }
    }


}
