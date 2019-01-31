package de.archilab.coalbase.learningoutcomeservice.core;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
public class UniqueId<T> implements Serializable {

  @Getter
  private final UUID uuid;

  protected UniqueId() {
    this.uuid = UUID.randomUUID();
  }

  public UniqueId(String idString) {
    if (idString == null) {
      this.uuid = UUID.randomUUID();
    } else {
      this.uuid = UUID.fromString(idString);
    }
  }

  public String toIdString() {
    return uuid.toString();
  }

}
