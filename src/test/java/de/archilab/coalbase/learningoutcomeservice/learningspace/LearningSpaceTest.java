package de.archilab.coalbase.learningoutcomeservice.learningspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.archilab.coalbase.learningoutcomeservice.learningoutcome.*;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Ability;

@Transactional
public class LearningSpaceTest {

  private static final LearningOutcome learningOutcome = new LearningOutcome(
      new Role("Student"), new Competence("actionValue", TaxonomyLevel.SYNTHESIS),
      Collections.<Requirement>emptyList(), Collections.<Ability>emptyList(),
      Collections.<Purpose>emptyList());

  private static final LearningOutcome secondLearningOutcome = new LearningOutcome(
          new Role("Student"), new Competence("newActionValue", TaxonomyLevel.ANALYSIS),
          Collections.<Requirement>emptyList(), Collections.<Ability>emptyList(),
          Collections.<Purpose>emptyList());

  @Test
  public void createLearningSpace() {
    LearningSpace learningSpace = new LearningSpace("name");
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "name");
  }

  @Test
  public void createLearningSpaceWithLearningOutcome() {
    LearningSpace learningSpace = new LearningSpace("name", learningOutcome);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "name");
    assertEquals(learningSpace.getLearningOutcome(), learningOutcome);
  }


  @Test
  public void createLearningSpaceWithRequirment() {
    LearningSpace learningSpaceWithoutRequirment = new LearningSpace("name");

    LearningSpace learningSpace = new LearningSpace("secondName",
        learningSpaceWithoutRequirment);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getRequirement(), learningSpaceWithoutRequirment);
  }

  @Test
  public void createLearningSpaceWithRequirmentAndLearningOutcome() {
    LearningSpace learningSpaceWithoutRequirment = new LearningSpace("name", learningOutcome);

    LearningSpace learningSpace = new LearningSpace("secondName", learningOutcome,
        learningSpaceWithoutRequirment);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "secondName");
    assertEquals(learningSpace.getLearningOutcome(), learningOutcome);
    assertEquals(learningSpace.getRequirement(), learningSpaceWithoutRequirment);
  }

  @Test
  public void updateLearningOutcome() {
    LearningSpace learningSpace = new LearningSpace("name", learningOutcome);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getLearningOutcome(), learningOutcome);
    learningSpace.setLearningOutcome(secondLearningOutcome);
    assertEquals(learningSpace.getLearningOutcome(), secondLearningOutcome);
  }

  @Test
  public void updateRequirment() {
    LearningSpace learningSpaceWithoutRequirment = new LearningSpace("name");
    LearningSpace learningSpace = new LearningSpace("secondName",
        learningSpaceWithoutRequirment);

    assertEquals(learningSpace.getRequirement(), learningSpaceWithoutRequirment);

    LearningSpace newLearningSpaceWithoutRequirment = new LearningSpace("newName");
    learningSpace.setRequirement(newLearningSpaceWithoutRequirment);
    assertEquals(learningSpace.getRequirement(), newLearningSpaceWithoutRequirment);
  }

  @Test
  public void updateTitle() {
    LearningSpace learningSpace = new LearningSpace("name");

    assertEquals(learningSpace.getTitle(), "name");
    learningSpace.setTitle("newTitle");
    assertEquals(learningSpace.getTitle(), "newTitle");
  }
}
