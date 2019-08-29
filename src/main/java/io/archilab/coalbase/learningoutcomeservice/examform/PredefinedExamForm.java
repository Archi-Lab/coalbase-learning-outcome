package io.archilab.coalbase.learningoutcomeservice.examform;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.coalbase.learningoutcomeservice.core.EntityWithUniqueId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(callSuper = true)
public class PredefinedExamForm extends EntityWithUniqueId<PredefinedExamForm> {

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
