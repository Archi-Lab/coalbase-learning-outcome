package de.archilab.coalbase.learningoutcomeservice.learningspace;

import javax.persistence.Entity;

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
public class LearningSpace {

  private String title;
  private LearningOutcome learningOutcome;
}
