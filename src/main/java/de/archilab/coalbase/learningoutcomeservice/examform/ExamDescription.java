package de.archilab.coalbase.learningoutcomeservice.examform;

import lombok.*;

import javax.persistence.Embeddable;

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
