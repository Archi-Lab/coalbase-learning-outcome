package de.archilab.coalbase.learningoutcomeservice.learningspace;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import de.archilab.coalbase.learningoutcomeservice.core.EntityWithUniqueId;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcome;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString(callSuper = true)
public class LearningSpace extends EntityWithUniqueId<LearningSpace> {

  private String title;

  private ExamForm examForm;

  @OneToOne(targetEntity = LearningOutcome.class, cascade = CascadeType.REMOVE)
  private LearningOutcome learningOutcome;

  @OneToOne(targetEntity = LearningSpace.class)
  private LearningSpace requirement;


  public LearningSpace(String title) {
    this.title = title;
  }

  public LearningSpace(String title, ExamForm examForm) {
    this.title = title;
    this.examForm = examForm;
  }

  public LearningSpace(String title, LearningSpace requirement) {
    this.title = title;
    this.requirement = requirement;
  }

  public LearningSpace(String title, LearningOutcome learningOutcome) {
    this.title = title;
    this.learningOutcome = learningOutcome;
  }
}
