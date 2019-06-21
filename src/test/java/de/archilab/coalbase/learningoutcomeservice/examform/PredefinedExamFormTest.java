package de.archilab.coalbase.learningoutcomeservice.examform;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class PredefinedExamFormTest {

  private static final ExamType type1 = new ExamType("Klausur");
  private static final ExamType type2 = new ExamType("Diskussion");
  private static final Schedule schedule = new Schedule("Am Anfang");
  private static final Duration duration = new Duration(10, 15, "Min");
  private static final ExamDescription description = new ExamDescription("Ist einfach");

  private static PredefinedExamForm createExamForm1() {
    List<Schedule> schedules = new ArrayList<>();
    schedules.add(schedule);

    return new PredefinedExamForm(type1, schedules, duration, description);
  }

  private static PredefinedExamForm createExamForm2() {
    List<Schedule> schedules = new ArrayList<>();
    schedules.add(schedule);

    return new PredefinedExamForm(type2, schedules, duration, description);
  }

  @Test
  public void createPredefinedExamForm() {
    PredefinedExamForm predefinedExamForm = createExamForm1();
    assertNotNull(predefinedExamForm);

    assertEquals(predefinedExamForm.getType(), type1);
    assertEquals(predefinedExamForm.getSchedules().size(), 1);
    assertEquals(predefinedExamForm.getDescription(), description);
    assertEquals(predefinedExamForm.getDuration().getMinValue(), duration.getMinValue());
    assertEquals(predefinedExamForm.getDuration().getMaxValue(), duration.getMaxValue());
    assertEquals(predefinedExamForm.getDuration().getUnit(), duration.getUnit());
  }

  @Test
  public void compareDifferentPredefinedExamForms() {
    PredefinedExamForm predefinedExamForm1 = createExamForm1();
    assertNotNull(predefinedExamForm1);

    assertEquals(predefinedExamForm1.getType(), type1);
    assertEquals(predefinedExamForm1.getSchedules().size(), 1);

    PredefinedExamForm predefinedExamForm2 = createExamForm2();
    assertNotEquals(predefinedExamForm1, predefinedExamForm2);
  }
}
