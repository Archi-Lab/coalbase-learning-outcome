package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import de.archilab.coalbase.learningoutcomeservice.core.DomainEvent;
import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;


public class LearningOutcomeDomainEvent extends DomainEvent {

  private final UniqueId<LearningOutcome> learningOutcomeIdentifier;
  private final LearningOutcomeEventType eventType;


  public LearningOutcomeDomainEvent(UniqueId<LearningOutcome> learningOutcomeIdentifier,
      LearningOutcomeEventType eventType) {
    this.learningOutcomeIdentifier = learningOutcomeIdentifier;
    this.eventType = eventType;
  }

  @Override
  public String getEventType() {
    return this.eventType.name();
  }

  public UniqueId<LearningOutcome> getLearningOutcomeIdentifier() {
    return learningOutcomeIdentifier;
  }
}
