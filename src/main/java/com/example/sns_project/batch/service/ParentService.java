package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.Child;
import com.example.sns_project.batch.entity.Parent;
import com.example.sns_project.batch.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    // Create
    public Parent createParent(String name) {
        Parent parent = new Parent(name);
        return parentRepository.save(parent);
    }

    // Read
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public Optional<Parent> getParentById(Long id) {
        return parentRepository.findById(id);
    }

    // Update
    public Parent updateParent(Long id, String name) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        parent.setName(name);
        return parentRepository.save(parent);
    }

    // Delete
    public void deleteParent(Long id) {
        parentRepository.deleteById(id);
    }

    // Add Child
    @Transactional
    public Parent addChildToParent(Long parentId, Child child) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        parent.addChild(child);
        return parentRepository.save(parent);
    }
}
