package de.archilab.coalbase.learningoutcomeservice.examform;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class Schedule {

    private static final int MAX_LENGTH = 100;

    private String value;

    public void checkValid() {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Schedule length is too long");
        }
    }
}
