package de.archilab.coalbase.learningoutcomeservice.learningoutcome;


import de.archilab.coalbase.learningoutcomeservice.core.EntityWithUniqueId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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
public class LearningOutcome extends EntityWithUniqueId<LearningOutcome> {

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
      throw new NoSuchElementException("There are no tools in this learning outcome.");
    }
    if (!this.tools.contains(tool)) {
      throw new NoSuchElementException(
          "The tool you want to remove is not present in this learning outcome.");
    }
    this.tools.remove(tool);
  }

  public List<Tool> getTools() {
    return Collections.unmodifiableList(this.tools);
  }

}
