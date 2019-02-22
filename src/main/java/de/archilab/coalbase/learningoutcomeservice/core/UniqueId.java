package de.archilab.coalbase.learningoutcomeservice.core;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
public class UniqueId<T> implements Serializable {

  private static final long serialVersionUID = -7931626090547229453L;
  @Getter
  private final UUID uuid;

  public UniqueId() {
    this.uuid = UUID.randomUUID();
  }

  public UniqueId(String idString) {
    if (idString == null) {
      this.uuid = UUID.randomUUID();
    } else {
      this.uuid = UUID.fromString(idString);
    }
  }

  @Override
  public String toString() {
    return this.uuid.toString();
  }

}
