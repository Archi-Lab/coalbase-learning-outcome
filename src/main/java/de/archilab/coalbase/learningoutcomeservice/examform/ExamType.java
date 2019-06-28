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
public class ExamType {

  private static final int MAX_LENGTH = 50;

  private String type;

  public void checkValid() {
    if (type.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("Type length is too long");
    }
  }
}
