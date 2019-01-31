package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class LearningOutcomeEventHandler {

  private final LearningOutcomeMessageProducer learningOutcomeMessageProducer;

  @Autowired
  public LearningOutcomeEventHandler(
      final LearningOutcomeMessageProducer learningOutcomeMessageProducer) {
    this.learningOutcomeMessageProducer = learningOutcomeMessageProducer;
  }

  @HandleAfterCreate
  public void handleLearningOutcomeCreate(LearningOutcome learningOutcome) {

  }

  @HandleAfterSave
  public void handleLearningOutcomeSave(LearningOutcome learningOutcome) {

  }

  @HandleAfterDelete
  public void handleLearningOutcomeDelete(LearningOutcome learningOutcome) {

  }
}
