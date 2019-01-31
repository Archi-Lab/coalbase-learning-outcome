package de.archilab.coalbase.learningoutcomeservice.core;

import java.util.UUID;

public abstract class DomainEvent {

  private final UUID eventID;

  public DomainEvent() {
    eventID = UUID.randomUUID();
  }

  public abstract String getEventType();

  public UUID getEventID() {
    return eventID;
  }
}
