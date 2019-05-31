package de.archilab.coalbase.learningoutcomeservice.course;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import de.archilab.coalbase.learningoutcomeservice.core.exceptions.EmptyListException;
import de.archilab.coalbase.learningoutcomeservice.learningspace.LearningSpace;

public class CourseTest {

  private static final String SHORT_TITLE = "Test short title";
  private static final String TITLE = "Test title";
  private static final String DESCRIPTION = "A test description";
  private static final LearningSpace LEARNING_SPACE = new LearningSpace("testLearningSpace");

  @Test
  public void createCourseTest() {
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(LEARNING_SPACE)));

    assertThat(course.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(TITLE);
    assertThat(course.getLearningSpaces()).contains(LEARNING_SPACE);
  }

  @Test
  public void addLearningSpaceToCourse() {
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION, null);

    assertThat(course.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(TITLE);
    assertThat(course.getLearningSpaces()).isNull();

    course.addLearningSpace(LEARNING_SPACE);
    assertThat(course.getLearningSpaces()).isNotNull();
    assertThat(course.getLearningSpaces()).contains(LEARNING_SPACE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addEmptyLearningSpaceToCourse() {
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION, null);

    assertThat(course.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(TITLE);
    assertThat(course.getLearningSpaces()).isNull();

    course.addLearningSpace(null);
  }

  @Test
  public void removeLearningSpaceFromCourse() {
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(LEARNING_SPACE)));

    assertThat(course.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(TITLE);
    assertThat(course.getLearningSpaces()).contains(LEARNING_SPACE);

    course.removeLearningSpace(LEARNING_SPACE);
    assertThat(course.getLearningSpaces()).isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeEmptyLearningSpaceFromCourse() {
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(LEARNING_SPACE)));

    assertThat(course.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(TITLE);
    assertThat(course.getLearningSpaces()).contains(LEARNING_SPACE);

    course.removeLearningSpace(null);
  }

  @Test(expected = EmptyListException.class)
  public void removeLearningSpaceFromEmptyCourse() {
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION, null);

    assertThat(course.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(TITLE);
    assertThat(course.getLearningSpaces()).isNull();

    course.removeLearningSpace(LEARNING_SPACE);
  }


}
