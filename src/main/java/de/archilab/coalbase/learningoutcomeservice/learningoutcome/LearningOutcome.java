package de.archilab.coalbase.learningoutcomeservice.learningoutcome;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import de.archilab.coalbase.learningoutcomeservice.core.EntityWithUniqueId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@EntityListeners(LearningOutcomeEventHandler.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(callSuper = true)
public class LearningOutcome extends EntityWithUniqueId<LearningOutcome> {

  @Setter
  private Role role;

  @Setter
  private Competence competence;

  @ElementCollection
  private List<Requirement> requirements;

  @ElementCollection
  private List<Ability> abilities;

  @ElementCollection
  private List<Purpose> purposes;


  public void addAbility(Ability ability) {
    if (this.abilities == null) {
      this.abilities = new ArrayList<>();
    }
    this.abilities.add(ability);
  }

  public void removeAbility(Ability ability) {
    if (this.abilities.isEmpty()) {
      throw new NoSuchElementException("There are no abilities in this learning outcome.");
    }
    if (!this.abilities.contains(ability)) {
      throw new NoSuchElementException(
          "The ability you want to remove is not present in this learning outcome.");
    }
    this.abilities.remove(ability);
  }

  public void addRequirement(Requirement requirement) {
    if (this.requirements == null) {
      this.requirements = new ArrayList<>();
    }
    this.requirements.add(requirement);
  }

  public void removeRequirement(Requirement requirement) {
    if (this.requirements.isEmpty()) {
      throw new NoSuchElementException("There are no requirements in this learning outcome.");
    }
    if (!this.requirements.contains(requirement)) {
      throw new NoSuchElementException(
          "The requirement you want to remove is not present in this learning outcome.");
    }
    this.requirements.remove(requirement);
  }

  public void addPurpose(Purpose purpose) {
    if (this.purposes == null) {
      this.purposes = new ArrayList<>();
    }
    this.purposes.add(purpose);
  }

  public void removePurpose(Purpose purpose) {
    if (this.purposes.isEmpty()) {
      throw new NoSuchElementException("There are no purposes in this learning outcome.");
    }
    if (!this.purposes.contains(purpose)) {
      throw new NoSuchElementException(
          "The purpose you want to remove is not present in this learning outcome.");
    }
    this.purposes.remove(purpose);
  }

  public List<Ability> getAbilities() {
    return Collections.unmodifiableList(this.abilities);
  }

  public List<Purpose> getPurposes() {
    return Collections.unmodifiableList(this.purposes);
  }
}
