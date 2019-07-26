package de.archilab.coalbase.learningoutcomeservice.examform;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class PredefinedExamFormTest {

  private static final ExamType TYPE_1 = new ExamType("Klausur");
  private static final ExamType TYPE_2 = new ExamType("Diskussion");
  private static final Schedule SCHEDULE = new Schedule("Am Anfang");
  private static final Scope SCOPE = new Scope(10, 15, "Min");
  private static final ExamDescription DESCRIPTION = new ExamDescription("Ist einfach");

  private static PredefinedExamForm createExamForm1() {
    List<Schedule> schedules = new ArrayList<>();
    schedules.add(SCHEDULE);

    return new PredefinedExamForm(TYPE_1, schedules, SCOPE, DESCRIPTION);
  }

  private static PredefinedExamForm createExamForm2() {
    List<Schedule> schedules = new ArrayList<>();
    schedules.add(SCHEDULE);

    return new PredefinedExamForm(TYPE_2, schedules, SCOPE, DESCRIPTION);
  }

  @Test
  public void createPredefinedExamForm() {
    PredefinedExamForm predefinedExamForm = createExamForm1();
    assertNotNull(predefinedExamForm);

    assertEquals(predefinedExamForm.getType(), TYPE_1);
    assertEquals(predefinedExamForm.getSchedules().size(), 1);
    assertEquals(predefinedExamForm.getDescription(), DESCRIPTION);
    assertEquals(predefinedExamForm.getScope().getMinValue(), SCOPE.getMinValue());
    assertEquals(predefinedExamForm.getScope().getMaxValue(), SCOPE.getMaxValue());
    assertEquals(predefinedExamForm.getScope().getUnit(), SCOPE.getUnit());
  }

  @Test
  public void compareDifferentPredefinedExamForms() {
    PredefinedExamForm predefinedExamForm1 = createExamForm1();
    assertNotNull(predefinedExamForm1);

    assertEquals(predefinedExamForm1.getType(), TYPE_1);
    assertEquals(predefinedExamForm1.getSchedules().size(), 1);

    PredefinedExamForm predefinedExamForm2 = createExamForm2();
    assertNotEquals(predefinedExamForm1, predefinedExamForm2);
  }
}
