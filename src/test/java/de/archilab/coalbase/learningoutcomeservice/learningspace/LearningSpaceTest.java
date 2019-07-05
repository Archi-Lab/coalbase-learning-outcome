package de.archilab.coalbase.learningoutcomeservice.learningspace;

import de.archilab.coalbase.learningoutcomeservice.examform.Scope;
import de.archilab.coalbase.learningoutcomeservice.examform.ExamDescription;
import de.archilab.coalbase.learningoutcomeservice.examform.ExamType;
import de.archilab.coalbase.learningoutcomeservice.examform.Schedule;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.*;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
public class LearningSpaceTest {

  private static final LearningOutcome LEARNING_OUTCOME = new LearningOutcome(
      new Role("Student"), new Competence("actionValue", TaxonomyLevel.SYNTHESIS),
      Collections.<Requirement>emptyList(), Collections.<Ability>emptyList(),
      Collections.<Purpose>emptyList());

  private static final LearningOutcome SECOND_LEARNING_OUTCOME = new LearningOutcome(
          new Role("Student"), new Competence("newActionValue", TaxonomyLevel.ANALYSIS),
          Collections.<Requirement>emptyList(), Collections.<Ability>emptyList(),
          Collections.<Purpose>emptyList());

  private static final ExamForm EXAM_FORM = createExamForm();

  private static ExamForm createExamForm() {
    ExamType type = new ExamType("Klausur");

    List<Schedule> schedules = new ArrayList<>();
    schedules.add(new Schedule("Am Anfang"));

    Scope scope = new Scope(10, 15, "Min");

    ExamDescription description = new ExamDescription("Ist einfach");

    return new ExamForm(type, schedules, scope, description);
  }

  @Test
  public void createLearningSpace() {
    LearningSpace learningSpace = new LearningSpace("name", EXAM_FORM);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "name");
    assertEquals(learningSpace.getExamForm(), EXAM_FORM);
  }

  @Test
  public void createLearningSpaceWithLearningOutcome() {
    LearningSpace learningSpace = new LearningSpace("name", LEARNING_OUTCOME);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "name");
    assertEquals(learningSpace.getLearningOutcome(), LEARNING_OUTCOME);
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
    LearningSpace learningSpaceWithoutRequirment = new LearningSpace("name", LEARNING_OUTCOME);

    LearningSpace learningSpace = new LearningSpace("secondName", EXAM_FORM, LEARNING_OUTCOME, learningSpaceWithoutRequirment);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "secondName");
    assertEquals(learningSpace.getLearningOutcome(), LEARNING_OUTCOME);
    assertEquals(learningSpace.getRequirement(), learningSpaceWithoutRequirment);
  }

  @Test
  public void updateLearningOutcome() {
    LearningSpace learningSpace = new LearningSpace("name", LEARNING_OUTCOME);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getLearningOutcome(), LEARNING_OUTCOME);
    learningSpace.setLearningOutcome(SECOND_LEARNING_OUTCOME);
    assertEquals(learningSpace.getLearningOutcome(), SECOND_LEARNING_OUTCOME);
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
