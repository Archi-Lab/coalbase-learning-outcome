package io.archilab.coalbase.learningoutcomeservice.examform;

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
public class Schedule {

  private static final int MAX_LENGTH = 100;

  private String value;

  public void checkValid() {
    if (this.value.length() > Schedule.MAX_LENGTH) {
      throw new IllegalArgumentException("Schedule length is too long");
    }
  }
}
