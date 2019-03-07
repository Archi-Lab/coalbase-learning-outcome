package de.archilab.coalbase.learningoutcomeservice.learningspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.archilab.coalbase.learningoutcomeservice.kafka.KafkaMessageProducer;

@Component
@RepositoryEventHandler(LearningSpace.class)
public class LearningSpaceEventHandler {

  private final String topic;
  private final KafkaMessageProducer kafkaMessageProducer;

  @Autowired
  public LearningSpaceEventHandler(@Value("${learning-space.topic}") final String topic,
      final KafkaMessageProducer kafkaMessageProducer) {
    this.kafkaMessageProducer = kafkaMessageProducer;
    this.topic = topic;
  }

  @HandleAfterCreate
  public void handleLearningSpaceCreate(LearningSpace learningSpace)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        this.buildLearningSpaceDomainEvent(learningSpace, LearningSpaceEventType.CREATED));
  }

  @HandleAfterSave
  public void handleLearningOutcomeSave(LearningSpace learningSpace)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        this.buildLearningSpaceDomainEvent(learningSpace, LearningSpaceEventType.UPDATED));
  }

  @HandleAfterDelete
  public void handleLearningOutcomeDelete(LearningSpace learningSpace)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        this.buildLearningSpaceDomainEvent(learningSpace, LearningSpaceEventType.DELETED));
  }

  private LearningSpaceDomainEvent buildLearningSpaceDomainEvent(
      LearningSpace learningSpace, LearningSpaceEventType eventType) {
    return new LearningSpaceDomainEvent(learningSpace.getId(), eventType);
  }

}
