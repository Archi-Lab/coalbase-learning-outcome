package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import de.archilab.coalbase.learningoutcomeservice.core.DomainEvent;
import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;

public class LearningOutcomeDomainEvent extends DomainEvent {

  private UniqueId<LearningOutcome> learningOutcomeIdentifier;
  
  @Override
  public String getEventType() {
    return null;
  }
}
