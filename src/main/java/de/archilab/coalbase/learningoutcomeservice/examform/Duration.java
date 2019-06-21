package de.archilab.coalbase.learningoutcomeservice.examform;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.Size;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class Duration {

    private static final int MAX_LENGTH = 30;

    private int minValue;
    private int maxValue;

    @Size(max = MAX_LENGTH)
    private String unit;


    @PrePersist
    @PreUpdate
    public void checkValid() {
        if (minValue < 0 || maxValue < 0 || (maxValue < minValue && maxValue != 0)) {
            throw new IllegalArgumentException("MaxValue has to be greater than MinValue and not negative");
        }
    }
}
