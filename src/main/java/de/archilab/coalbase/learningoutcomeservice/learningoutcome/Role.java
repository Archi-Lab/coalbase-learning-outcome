package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class Role {

  private String value;

  public static boolean isValid(Role role) {
    return !role.getValue().isEmpty();
  }

}
