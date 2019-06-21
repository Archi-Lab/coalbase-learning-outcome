package de.archilab.coalbase.learningoutcomeservice.examform;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class ExamType {

    private static final int MAX_LENGTH = 50;

    @Size(max = MAX_LENGTH)
    private String type;
}
