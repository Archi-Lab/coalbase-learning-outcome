package de.archilab.coalbase.learningoutcomeservice.course;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import de.archilab.coalbase.learningoutcomeservice.core.EntityWithUniqueId;
import de.archilab.coalbase.learningoutcomeservice.learningspace.LearningSpace;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@ToString(callSuper = true)
public class Course extends EntityWithUniqueId<Course> {

  private String title;
  private String description;

  @OneToMany(mappedBy = "id")
  private List<LearningSpace> learningSpaces;

  public void addLearningSpace(LearningSpace learningSpace) {
    if (learningSpace != null) {
      if (this.learningSpaces == null) {
        this.learningSpaces = new ArrayList<>();
      }
      this.learningSpaces.add(learningSpace);
    }
  }

  public void removeLearningSpace(LearningSpace learningSpace) {
    if (this.learningSpaces != null && !this.learningSpaces.isEmpty() && learningSpace != null) {
      this.learningSpaces.remove(learningSpace);
    }
  }

}
