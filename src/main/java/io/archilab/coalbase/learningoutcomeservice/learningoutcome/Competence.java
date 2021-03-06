package io.archilab.coalbase.learningoutcomeservice.learningoutcome;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
@Getter
public class Competence {

  private String action;
  private TaxonomyLevel taxonomyLevel;

  public static boolean isValid(Competence competence) {
    return !competence.getAction().isEmpty() && competence.getTaxonomyLevel() != null;
  }

}
