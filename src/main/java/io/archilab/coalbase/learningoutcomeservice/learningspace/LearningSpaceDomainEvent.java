package io.archilab.coalbase.learningoutcomeservice.learningspace;

import io.archilab.coalbase.learningoutcomeservice.core.DomainEvent;
import io.archilab.coalbase.learningoutcomeservice.core.UniqueId;


public class LearningSpaceDomainEvent extends DomainEvent {

  private final UniqueId<LearningSpace> learningSpaceIdentifier;
  private final LearningSpaceEventType eventType;

  protected LearningSpaceDomainEvent() {
    this.learningSpaceIdentifier = null;
    this.eventType = null;
  }

  public LearningSpaceDomainEvent(UniqueId<LearningSpace> learningSpaceIdentifier,
      LearningSpaceEventType eventType) {
    this.learningSpaceIdentifier = learningSpaceIdentifier;
    this.eventType = eventType;
  }


  @Override
  public String getEventType() {
    return this.eventType.name();
  }

  public UniqueId<LearningSpace> getLearningSpaceIdentifier() {
    return this.learningSpaceIdentifier;
  }
}
