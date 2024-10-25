package com.example.sns_project.batch.service;

import com.example.sns_project.batch.entity.Child;
import com.example.sns_project.batch.entity.Parent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class ChildServiceTest {

    @Autowired
    private ChildService childService;

    @Autowired
    private ParentService parentService;

    private Parent parent;

    @BeforeEach
    public void setUp() {
        parent = parentService.createParent("Parent for Child");
    }

    @Test
    @DisplayName("Child create 테스트")
    public void testCreateChild() {
        Child child = childService.createChild("Test Child", parent);
        assertNotNull(child.getId());
        assertEquals("Test Child", child.getName());
        assertEquals(parent.getId(), child.getParent().getId());
    }

    @Test
    @DisplayName("Child select 테스트")
    public void testReadChild() {
        Child child = childService.createChild("Test Child", parent);
        Child foundChild = childService.getChildById(child.getId()).orElse(null);
        assertNotNull(foundChild);
        assertEquals("Test Child", foundChild.getName());
    }

    @Test
    @DisplayName("Child update 테스트")
    public void testUpdateChild() {
        Child child = childService.createChild("Test Child", parent);
        Child updatedChild = childService.updateChild(child.getId(), "Updated Child");
        assertEquals("Updated Child", updatedChild.getName());
    }

    @Test
    @DisplayName("Child delete 테스트")
    public void testDeleteChild() {
        Child child = childService.createChild("Test Child", parent);
        childService.deleteChild(child.getId());
        assertFalse(childService.getChildById(child.getId()).isPresent());
    }
}
