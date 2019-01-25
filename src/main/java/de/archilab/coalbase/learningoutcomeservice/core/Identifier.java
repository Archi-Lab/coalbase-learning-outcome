package de.archilab.coalbase.learningoutcomeservice.core;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;


@MappedSuperclass
public abstract class Identifier implements Serializable {

  @JsonIgnore
  protected UUID id = UUID.randomUUID();

  @java.beans.ConstructorProperties({"id"})
  public Identifier(UUID id) {
    this.id = id;
  }

  protected Identifier() {
  }

  @Override
  public String toString() {
    return this.id.toString();
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Identifier)) {
      return false;
    }
    final Identifier other = (Identifier) o;
    if (!other.canEqual((Object) this)) {
      return false;
    }
    final Object this$id = this.id;
    final Object other$id = other.id;
    if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $id = this.id;
    result = result * PRIME + ($id == null ? 43 : $id.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Identifier;
  }
}
