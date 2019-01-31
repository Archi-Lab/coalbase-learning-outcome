package de.archilab.coalbase.learningoutcomeservice.infrastructure;

import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

import lombok.Getter;

@MappedSuperclass
public abstract class EntityWithUniqueId<T>{

  protected EntityWithUniqueId(){
  }

  @EmbeddedId
  @Getter
  private UniqueId<T> id;

  //Equalcheck only on id, not other fields!
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof EntityWithUniqueId)) {
      return false;
    }
    final EntityWithUniqueId other = (EntityWithUniqueId) o;
    if (!other.canEqual((Object) this)) {
      return false;
    }
    final Object this$id = this.getId();
    final Object other$id = other.getId();

    return this$id == null ? other$id == null : this$id.equals(other$id);
  }

  //Hashcode only on id, not other fields!
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $id = this.getId();
    result = result * PRIME + ($id == null ? 43 : $id.hashCode());
    return result;
  }

  private boolean canEqual(Object other) {
    return other instanceof EntityWithUniqueId;
  }
}
