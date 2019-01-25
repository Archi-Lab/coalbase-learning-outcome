package de.archilab.coalbase.learningoutcomeservice.core;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class Identifier implements Serializable {

  @JsonIgnore
  protected UUID id = UUID.randomUUID();

  @Override
  public String toString() {
    return this.id.toString();
  }
}
