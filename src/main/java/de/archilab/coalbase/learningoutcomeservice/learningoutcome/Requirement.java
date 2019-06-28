package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class Requirement {

  private String value;
  private TaxonomyLevel taxonomyLevel;

  public static boolean isValid(Requirement requirement) {
    return !requirement.getValue().isEmpty() && requirement.getTaxonomyLevel() != null;
  }

}
