package de.archilab.coalbase.learningoutcomeservice.examform;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class PredefinedExamFormTest {

  private static PredefinedExamForm createExamForm1() {
    ExamType type = new ExamType("Klausur");

    List<Schedule> schedules = new ArrayList<>();
    schedules.add(new Schedule("Am Anfang"));

    Duration duration = new Duration(10, 15, "Min");

    ExamDescription description = new ExamDescription("Ist einfach");

    return new PredefinedExamForm(type, schedules, duration, description);
  }

  private static PredefinedExamForm createExamForm2() {
    ExamType type = new ExamType("Diskussion");

    List<Schedule> schedules = new ArrayList<>();
    schedules.add(new Schedule("Am Anfang"));

    Duration duration = new Duration(10, 15, "Min");

    ExamDescription description = new ExamDescription("Ist einfach");

    return new PredefinedExamForm(type, schedules, duration, description);
  }

  @Test
  public void createPredefinedExamForm() {
    PredefinedExamForm predefinedExamForm = createExamForm1();
    assertNotNull(predefinedExamForm);

    assertEquals(predefinedExamForm.getType(), new ExamType("Klausur"));
    assertEquals(predefinedExamForm.getSchedules().size(), 1);
    assertEquals(predefinedExamForm.getDescription(), new ExamDescription("Ist einfach"));
    assertEquals(predefinedExamForm.getDuration().getMinValue(), 10);
    assertEquals(predefinedExamForm.getDuration().getMaxValue(), 15);
    assertEquals(predefinedExamForm.getDuration().getUnit(), "Min");
  }

  @Test
  public void compareDifferentPredefinedExamForms() {
    PredefinedExamForm predefinedExamForm1 = createExamForm1();
    assertNotNull(predefinedExamForm1);

    assertEquals(predefinedExamForm1.getType(), new ExamType("Klausur"));
    assertEquals(predefinedExamForm1.getSchedules().size(), 1);

    PredefinedExamForm predefinedExamForm2 = createExamForm2();
    assertNotEquals(predefinedExamForm1, predefinedExamForm2);
  }
}
