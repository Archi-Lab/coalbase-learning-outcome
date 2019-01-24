package de.archilab.coalbase.learningoutcomeservice.learningoutcome;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;


import de.archilab.coalbase.learningoutcomeservice.core.AbstractEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(callSuper = true)
public class LearningOutcome extends AbstractEntity {

  @Setter
  private Competence competence;

  @ElementCollection
  private List<Tool> tools;

  @Setter
  private Purpose purpose;

  public void addTool(Tool tool) {
    if (this.tools == null) {
      this.tools = new ArrayList();
    }
    this.tools.add(tool);
  }

  public void removeTool(Tool tool) {
    if (this.tools.isEmpty()) {
      throw new RuntimeException("There are no tools in this learning outcome.");
    }
    if (!this.tools.contains(tool)) {
      throw new RuntimeException(
          "The tool you want to remove is not present in this learning outcome.");
    }
    this.tools.remove(tool);
  }

  public List<Tool> getTools() {
    return Collections.unmodifiableList(this.tools);
  }

}
