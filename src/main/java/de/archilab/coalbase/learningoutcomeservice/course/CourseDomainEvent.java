package de.archilab.coalbase.learningoutcomeservice.course;

import de.archilab.coalbase.learningoutcomeservice.core.DomainEvent;
import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;

public class CourseDomainEvent extends DomainEvent {

  private final UniqueId<Course> courseIdentifier;
  private final CourseEventType eventType;

  protected CourseDomainEvent() {
    this.courseIdentifier = null;
    this.eventType = null;
  }

  public CourseDomainEvent(UniqueId<Course> courseIdentifier,
      CourseEventType eventType) {
    this.courseIdentifier = courseIdentifier;
    this.eventType = eventType;
  }

  @Override
  public String getEventType() {
    return this.eventType.name();
  }

  public UniqueId<Course> getCourseIdentifier() {
    return this.courseIdentifier;
  }

}
