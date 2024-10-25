package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.Child;
import com.example.sns_project.batch.entity.Parent;
import com.example.sns_project.batch.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    // Create
    public Child createChild(String name, Parent parent) {
        Child child = new Child(name);
        child.setParent(parent);
        return childRepository.save(child);
    }

    // Read
    public List<Child> getAllChildren() {
        return childRepository.findAll();
    }

    public Optional<Child> getChildById(Long id) {
        return childRepository.findById(id);
    }

    // Update
    public Child updateChild(Long id, String name) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        child.setName(name);
        return childRepository.save(child);
    }

    // Delete
    public void deleteChild(Long id) {
        childRepository.deleteById(id);
    }
}
