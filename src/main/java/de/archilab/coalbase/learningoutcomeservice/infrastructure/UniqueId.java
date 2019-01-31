package de.archilab.coalbase.learningoutcomeservice.infrastructure;

import java.io.Serializable;
import java.util.UUID;

public class UniqueId<T> implements Serializable {

  UniqueId(){
    this.uuid = UUID.randomUUID();
  }

  UniqueId(String idString){
    if(idString == null){
      this.uuid = UUID.randomUUID();
    } else {
      this.uuid = UUID.fromString(idString);
    }
  }

  private final UUID uuid;

  public String toIdString(){
    return uuid.toString();
  }

}
