package io.archilab.coalbase.learningoutcomeservice.learningspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.archilab.coalbase.learningoutcomeservice.examform.ExamDescription;
import io.archilab.coalbase.learningoutcomeservice.examform.ExamType;
import io.archilab.coalbase.learningoutcomeservice.examform.Schedule;
import io.archilab.coalbase.learningoutcomeservice.examform.Scope;
import io.archilab.coalbase.learningoutcomeservice.learningoutcome.Ability;
import io.archilab.coalbase.learningoutcomeservice.learningoutcome.Competence;
import io.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcome;
import io.archilab.coalbase.learningoutcomeservice.learningoutcome.Purpose;
import io.archilab.coalbase.learningoutcomeservice.learningoutcome.Requirement;
import io.archilab.coalbase.learningoutcomeservice.learningoutcome.Role;
import io.archilab.coalbase.learningoutcomeservice.learningoutcome.TaxonomyLevel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

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

  private static final ExamForm EXAM_FORM = LearningSpaceTest.createExamForm();

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
    LearningSpace learningSpace = new LearningSpace("name", LearningSpaceTest.EXAM_FORM);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "name");
    assertEquals(learningSpace.getExamForm(), LearningSpaceTest.EXAM_FORM);
  }

  @Test
  public void createLearningSpaceWithLearningOutcome() {
    LearningSpace learningSpace = new LearningSpace("name", LearningSpaceTest.LEARNING_OUTCOME);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "name");
    assertEquals(learningSpace.getLearningOutcome(), LearningSpaceTest.LEARNING_OUTCOME);
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
    LearningSpace learningSpaceWithoutRequirment = new LearningSpace("name",
        LearningSpaceTest.LEARNING_OUTCOME);

    LearningSpace learningSpace = new LearningSpace("secondName", LearningSpaceTest.EXAM_FORM,
        LearningSpaceTest.LEARNING_OUTCOME,
        learningSpaceWithoutRequirment);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getTitle(), "secondName");
    assertEquals(learningSpace.getLearningOutcome(), LearningSpaceTest.LEARNING_OUTCOME);
    assertEquals(learningSpace.getRequirement(), learningSpaceWithoutRequirment);
  }

  @Test
  public void updateLearningOutcome() {
    LearningSpace learningSpace = new LearningSpace("name", LearningSpaceTest.LEARNING_OUTCOME);
    assertNotNull(learningSpace);

    assertEquals(learningSpace.getLearningOutcome(), LearningSpaceTest.LEARNING_OUTCOME);
    learningSpace.setLearningOutcome(LearningSpaceTest.SECOND_LEARNING_OUTCOME);
    assertEquals(learningSpace.getLearningOutcome(), LearningSpaceTest.SECOND_LEARNING_OUTCOME);
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
