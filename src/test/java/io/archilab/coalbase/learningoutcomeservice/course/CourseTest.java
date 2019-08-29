package io.archilab.coalbase.learningoutcomeservice.course;

import static org.assertj.core.api.Assertions.assertThat;

import io.archilab.coalbase.learningoutcomeservice.core.exceptions.EmptyListException;
import io.archilab.coalbase.learningoutcomeservice.learningspace.LearningSpace;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;

public class CourseTest {

  private static final String SHORT_TITLE = "Test short title";
  private static final String TITLE = "Test title";
  private static final String DESCRIPTION = "A test description";
  private static final LearningSpace LEARNING_SPACE = new LearningSpace("testLearningSpace");

  @Test
  public void createCourseTest() {
    Course course = new Course(CourseTest.SHORT_TITLE, CourseTest.TITLE, CourseTest.DESCRIPTION,
        new ArrayList<>(Arrays.asList(CourseTest.LEARNING_SPACE)));

    assertThat(course.getDescription()).isEqualTo(CourseTest.DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(CourseTest.TITLE);
    assertThat(course.getLearningSpaces()).contains(CourseTest.LEARNING_SPACE);
  }

  @Test
  public void addLearningSpaceToCourse() {
    Course course = new Course(CourseTest.SHORT_TITLE, CourseTest.TITLE, CourseTest.DESCRIPTION, null);

    assertThat(course.getDescription()).isEqualTo(CourseTest.DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(CourseTest.TITLE);
    assertThat(course.getLearningSpaces()).isNull();

    course.addLearningSpace(CourseTest.LEARNING_SPACE);
    assertThat(course.getLearningSpaces()).isNotNull();
    assertThat(course.getLearningSpaces()).contains(CourseTest.LEARNING_SPACE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addEmptyLearningSpaceToCourse() {
    Course course = new Course(CourseTest.SHORT_TITLE, CourseTest.TITLE, CourseTest.DESCRIPTION, null);

    assertThat(course.getDescription()).isEqualTo(CourseTest.DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(CourseTest.TITLE);
    assertThat(course.getLearningSpaces()).isNull();

    course.addLearningSpace(null);
  }

  @Test
  public void removeLearningSpaceFromCourse() {
    Course course = new Course(CourseTest.SHORT_TITLE, CourseTest.TITLE, CourseTest.DESCRIPTION,
        new ArrayList<>(Arrays.asList(CourseTest.LEARNING_SPACE)));

    assertThat(course.getDescription()).isEqualTo(CourseTest.DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(CourseTest.TITLE);
    assertThat(course.getLearningSpaces()).contains(CourseTest.LEARNING_SPACE);

    course.removeLearningSpace(CourseTest.LEARNING_SPACE);
    assertThat(course.getLearningSpaces()).isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeEmptyLearningSpaceFromCourse() {
    Course course = new Course(CourseTest.SHORT_TITLE, CourseTest.TITLE, CourseTest.DESCRIPTION,
        new ArrayList<>(Arrays.asList(CourseTest.LEARNING_SPACE)));

    assertThat(course.getDescription()).isEqualTo(CourseTest.DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(CourseTest.TITLE);
    assertThat(course.getLearningSpaces()).contains(CourseTest.LEARNING_SPACE);

    course.removeLearningSpace(null);
  }

  @Test(expected = EmptyListException.class)
  public void removeLearningSpaceFromEmptyCourse() {
    Course course = new Course(CourseTest.SHORT_TITLE, CourseTest.TITLE, CourseTest.DESCRIPTION, null);

    assertThat(course.getDescription()).isEqualTo(CourseTest.DESCRIPTION);
    assertThat(course.getTitle()).isEqualTo(CourseTest.TITLE);
    assertThat(course.getLearningSpaces()).isNull();

    course.removeLearningSpace(CourseTest.LEARNING_SPACE);
  }


}
