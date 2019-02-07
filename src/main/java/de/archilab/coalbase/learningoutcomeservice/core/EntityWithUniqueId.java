package de.archilab.coalbase.learningoutcomeservice.core;

import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@MappedSuperclass
public abstract class EntityWithUniqueId<T> {

  @EmbeddedId
  @Getter
  private UniqueId<T> id;

  protected EntityWithUniqueId() {
    this.id = new UniqueId<>();
  }

}
