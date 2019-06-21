package de.archilab.coalbase.learningoutcomeservice.examform;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.archilab.coalbase.learningoutcomeservice.core.EntityWithUniqueId;
import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.Collections;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(callSuper = true)
public class PredefinedExamForm extends EntityWithUniqueId<PredefinedExamForm> {

    @JsonUnwrapped
    private ExamType type;

    @ElementCollection
    private List<Schedule> schedules;

    private Duration duration;

    @JsonUnwrapped
    private ExamDescription description;

    public List<Schedule> getSchedules() {
        return Collections.unmodifiableList(schedules);
    }
}
