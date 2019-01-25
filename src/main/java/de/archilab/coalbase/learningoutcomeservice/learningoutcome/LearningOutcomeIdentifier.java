package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import java.util.UUID;

import javax.persistence.Embeddable;

import de.archilab.coalbase.learningoutcomeservice.core.Identifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class LearningOutcomeIdentifier extends Identifier {

  public LearningOutcomeIdentifier(UUID uuid) {
    super(uuid);
  }
}
