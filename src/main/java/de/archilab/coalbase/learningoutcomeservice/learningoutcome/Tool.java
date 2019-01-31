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
public class Tool {

  private String value;

  public static boolean isValid(Tool tool) {
    return !tool.getValue().isEmpty();
  }
}
