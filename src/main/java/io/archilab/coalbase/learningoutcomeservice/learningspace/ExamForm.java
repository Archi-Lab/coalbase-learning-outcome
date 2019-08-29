package io.archilab.coalbase.learningoutcomeservice.learningspace;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.coalbase.learningoutcomeservice.examform.ExamDescription;
import io.archilab.coalbase.learningoutcomeservice.examform.ExamType;
import io.archilab.coalbase.learningoutcomeservice.examform.Schedule;
import io.archilab.coalbase.learningoutcomeservice.examform.Scope;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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

  private Scope scope;

  @JsonUnwrapped
  private ExamDescription description;

  public List<Schedule> getSchedules() {
    return Collections.unmodifiableList(this.schedules);
  }

  @PrePersist
  @PreUpdate
  public void checkValid() {
    this.type.checkValid();
    for (Schedule schedule : this.schedules) {
      schedule.checkValid();
    }
    this.scope.checkValid();
    this.description.checkValid();
  }
}
