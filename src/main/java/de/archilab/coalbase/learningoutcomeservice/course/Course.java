package de.archilab.coalbase.learningoutcomeservice.course;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.archilab.coalbase.learningoutcomeservice.core.EntityWithUniqueId;
import de.archilab.coalbase.learningoutcomeservice.core.exceptions.EmptyListException;
import de.archilab.coalbase.learningoutcomeservice.learningspace.LearningSpace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class Course extends EntityWithUniqueId<Course> {

  private String shortTitle;
  private String title;
  private String description;
  @JsonIgnore
  private String author;

  @OneToMany(targetEntity = LearningSpace.class)
  @JoinColumn(name = "course_uuid")
  private List<LearningSpace> learningSpaces;

  public Course(String shortTitle, String title, String description,
      List<LearningSpace> learningSpaces) {
    this.shortTitle = shortTitle;
    this.title = title;
    this.description = description;
    this.learningSpaces = learningSpaces;
  }

  public void addLearningSpace(LearningSpace learningSpace) {
    if (learningSpace != null) {
      if (this.learningSpaces == null) {
        this.learningSpaces = new ArrayList<>();
      }
      this.learningSpaces.add(learningSpace);
    } else {
      throw new IllegalArgumentException("given learningSpace is null");
    }
  }

  public void removeLearningSpace(LearningSpace learningSpace) {
    if (learningSpace != null) {
      if (this.learningSpaces != null && !this.learningSpaces.isEmpty()) {
        this.learningSpaces.remove(learningSpace);
      } else {
        throw new EmptyListException("List of LearningSpaces is empty");
      }
    } else {
      throw new IllegalArgumentException("given learningSpace is null");
    }
  }

}
