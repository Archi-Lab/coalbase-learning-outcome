package de.archilab.coalbase.learningoutcomeservice.core;

import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@MappedSuperclass
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntity {

  @Id
  @JsonIgnore
  private UUID id;
}
