package com.example.sns_project.batch.controller;

import com.example.sns_project.batch.entity.Child;
import com.example.sns_project.batch.entity.Parent;
import com.example.sns_project.batch.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/parents")
public class ParentController {

    private final ParentService parentService;

    // Create Parent
    @PostMapping
    public Parent createParent(@RequestParam String name) {
        return parentService.createParent(name);
    }

    // Read all Parents
    @GetMapping
    public List<Parent> getAllParents() {
        return parentService.getAllParents();
    }

    // Read Parent by ID
    @GetMapping("/{id}")
    public Optional<Parent> getParentById(@PathVariable Long id) {
        return parentService.getParentById(id);
    }

    // Update Parent
    @PutMapping("/{id}")
    public Parent updateParent(@PathVariable Long id, @RequestParam String name) {
        return parentService.updateParent(id, name);
    }

    // Delete Parent
    @DeleteMapping("/{id}")
    public void deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
    }

    // Add Child to Parent
    @PostMapping("/{parentId}/children")
    public Parent addChildToParent(@PathVariable Long parentId, @RequestBody Child child) {
        return parentService.addChildToParent(parentId, child);
    }
}
