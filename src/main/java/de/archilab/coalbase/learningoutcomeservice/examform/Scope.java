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
public class Scope {

  private static final int MAX_LENGTH = 50;

  private int minValue;
  private int maxValue;

  private String unit;


  public void checkValid() {
    if (unit.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("Unit length is too long");
    }

    if (minValue < 0 || maxValue < 0 || (maxValue < minValue && maxValue != 0)) {
      throw new IllegalArgumentException(
          "MaxValue has to be greater than MinValue and not negative");
    }
  }
}
