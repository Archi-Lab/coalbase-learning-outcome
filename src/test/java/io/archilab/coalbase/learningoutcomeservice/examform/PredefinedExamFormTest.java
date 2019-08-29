package io.archilab.coalbase.learningoutcomeservice.examform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PredefinedExamFormTest {

  private static final ExamType TYPE_1 = new ExamType("Klausur");
  private static final ExamType TYPE_2 = new ExamType("Diskussion");
  private static final Schedule SCHEDULE = new Schedule("Am Anfang");
  private static final Scope SCOPE = new Scope(10, 15, "Min");
  private static final ExamDescription DESCRIPTION = new ExamDescription("Ist einfach");

  private static PredefinedExamForm createExamForm1() {
    List<Schedule> schedules = new ArrayList<>();
    schedules.add(PredefinedExamFormTest.SCHEDULE);

    return new PredefinedExamForm(PredefinedExamFormTest.TYPE_1, schedules,
        PredefinedExamFormTest.SCOPE, PredefinedExamFormTest.DESCRIPTION);
  }

  private static PredefinedExamForm createExamForm2() {
    List<Schedule> schedules = new ArrayList<>();
    schedules.add(PredefinedExamFormTest.SCHEDULE);

    return new PredefinedExamForm(PredefinedExamFormTest.TYPE_2, schedules,
        PredefinedExamFormTest.SCOPE, PredefinedExamFormTest.DESCRIPTION);
  }

  @Test
  public void createPredefinedExamForm() {
    PredefinedExamForm predefinedExamForm = PredefinedExamFormTest.createExamForm1();
    assertNotNull(predefinedExamForm);

    assertEquals(predefinedExamForm.getType(), PredefinedExamFormTest.TYPE_1);
    assertEquals(predefinedExamForm.getSchedules().size(), 1);
    assertEquals(predefinedExamForm.getDescription(), PredefinedExamFormTest.DESCRIPTION);
    assertEquals(predefinedExamForm.getScope().getMinValue(), PredefinedExamFormTest.SCOPE.getMinValue());
    assertEquals(predefinedExamForm.getScope().getMaxValue(), PredefinedExamFormTest.SCOPE.getMaxValue());
    assertEquals(predefinedExamForm.getScope().getUnit(), PredefinedExamFormTest.SCOPE.getUnit());
  }

  @Test
  public void compareDifferentPredefinedExamForms() {
    PredefinedExamForm predefinedExamForm1 = PredefinedExamFormTest.createExamForm1();
    assertNotNull(predefinedExamForm1);

    assertEquals(predefinedExamForm1.getType(), PredefinedExamFormTest.TYPE_1);
    assertEquals(predefinedExamForm1.getSchedules().size(), 1);

    PredefinedExamForm predefinedExamForm2 = PredefinedExamFormTest.createExamForm2();
    assertNotEquals(predefinedExamForm1, predefinedExamForm2);
  }
}
