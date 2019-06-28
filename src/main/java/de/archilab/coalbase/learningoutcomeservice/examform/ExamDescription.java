package de.archilab.coalbase.learningoutcomeservice.examform;

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
public class ExamDescription {

  private static final int MAX_LENGTH = 2000;

  private String description;

  public void checkValid() {
    if (description.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("Description length is too long");
    }
  }
}
