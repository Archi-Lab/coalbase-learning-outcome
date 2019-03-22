package de.archilab.coalbase.learningoutcomeservice.course;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.util.Arrays;

import de.archilab.coalbase.learningoutcomeservice.learningspace.LearningSpace;

public class CourseTest {

  private static final String TITLE = "Test title";
  private static final String DESCRIPTION = "A test description";
  private static final LearningSpace LEARNING_SPACE = new LearningSpace("testLearningSpace");

  @Test
  public void createCourseTest() {
    Course course = new Course(TITLE, DESCRIPTION, Arrays.asList(LEARNING_SPACE));

    assertThat(course.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(TITLE);
    assertThat(course.getLearningSpaces()).contains(LEARNING_SPACE);
  }

}
