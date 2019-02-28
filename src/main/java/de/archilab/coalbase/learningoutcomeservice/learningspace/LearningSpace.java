package de.archilab.coalbase.learningoutcomeservice.learningspace;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import de.archilab.coalbase.learningoutcomeservice.core.EntityWithUniqueId;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcome;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString(callSuper = true)
public class LearningSpace extends EntityWithUniqueId<LearningSpace> {

  @NonNull
  private String title;
  @NonNull
  @OneToOne(targetEntity = LearningOutcome.class)
  private LearningOutcome learningOutcome;
  @OneToOne(targetEntity = LearningSpace.class)
  private LearningSpace requirement;
}
