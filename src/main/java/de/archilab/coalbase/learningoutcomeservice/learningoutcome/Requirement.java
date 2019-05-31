package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import lombok.*;

import javax.persistence.Embeddable;

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
