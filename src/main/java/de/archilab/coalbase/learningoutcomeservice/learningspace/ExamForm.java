package de.archilab.coalbase.learningoutcomeservice.learningspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import de.archilab.coalbase.learningoutcomeservice.examform.Duration;
import de.archilab.coalbase.learningoutcomeservice.examform.ExamDescription;
import de.archilab.coalbase.learningoutcomeservice.examform.ExamType;
import de.archilab.coalbase.learningoutcomeservice.examform.Schedule;
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
public class ExamForm {

  @JsonUnwrapped
  private ExamType type;

  @ElementCollection
  private List<Schedule> schedules = new ArrayList<>();

  private Duration duration;

  @JsonUnwrapped
  private ExamDescription description;

  public List<Schedule> getSchedules() {
    return Collections.unmodifiableList(schedules);
  }

  @PrePersist
  @PreUpdate
  public void checkValid() {
    type.checkValid();
    for (Schedule schedule : schedules) {
      schedule.checkValid();
    }
    duration.checkValid();
    description.checkValid();
  }
}
