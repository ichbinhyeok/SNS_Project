package com.example.sns_project.batch.controller;

import com.example.sns_project.batch.entity.Child;
import com.example.sns_project.batch.entity.Parent;
import com.example.sns_project.batch.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/children")
public class ChildController {

    private final ChildService childService;

    // Create Child
    @PostMapping
    public Child createChild(@RequestParam String name, @RequestParam Long parentId) {
        Parent parent = new Parent();
        parent.setId(parentId);
        return childService.createChild(name, parent);
    }

    // Read all Children
    @GetMapping
    public List<Child> getAllChildren() {
        return childService.getAllChildren();
    }

    // Read Child by ID
    @GetMapping("/{id}")
    public Optional<Child> getChildById(@PathVariable Long id) {
        return childService.getChildById(id);
    }

    // Update Child
    @PutMapping("/{id}")
    public Child updateChild(@PathVariable Long id, @RequestParam String name) {
        return childService.updateChild(id, name);
    }

    // Delete Child
    @DeleteMapping("/{id}")
    public void deleteChild(@PathVariable Long id) {
        childService.deleteChild(id);
    }
}
