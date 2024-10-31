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
public class ParentServiceTest {

    @Autowired
    private ParentService parentService;

    private Parent testParent;

    @BeforeEach
    public void setUp() {
        testParent = parentService.createParent("Test Parent");
    }

    @Test
    @DisplayName("Parent create 테스트")
    public void testCreateParent() {
        Parent parent = parentService.createParent("New Parent");
        assertNotNull(parent.getId());
        assertEquals("New Parent", parent.getName());
    }

    @Test
    @DisplayName("Parent select 테스트")
    public void testReadParent() {
        Parent foundParent = parentService.getParentById(testParent.getId()).orElse(null);
        assertNotNull(foundParent);
        assertEquals("Test Parent", foundParent.getName());
    }

    @Test
    @DisplayName("Parent update 테스트")
    public void testUpdateParent() {
        Parent updatedParent = parentService.updateParent(testParent.getId(), "Updated Parent");
        assertEquals("Updated Parent", updatedParent.getName());
    }

    @Test
    @DisplayName("Parent delete 테스트")
    public void testDeleteParent() {
        parentService.deleteParent(testParent.getId());
        assertFalse(parentService.getParentById(testParent.getId()).isPresent());
    }

    @Test
    @DisplayName("add child to parent 테스트")
    public void testAddChildToParent() {
        Child child = new Child("Child 1");
        Parent updatedParent = parentService.addChildToParent(testParent.getId(), child);
        assertEquals(1, updatedParent.getChildren().size());
        assertEquals("Child 1", updatedParent.getChildren().get(0).getName());
    }
}
