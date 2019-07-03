package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.archilab.coalbase.learningoutcomeservice.kafka.KafkaMessageProducer;

@Component
public class LearningOutcomeEventHandler {

  private final String topic;
  private final KafkaMessageProducer kafkaMessageProducer;

  @Autowired
  public LearningOutcomeEventHandler(
      @Value("${learning-outcome.topic}") final String topic,
      final KafkaMessageProducer kafkaMessageProducer) {
    this.kafkaMessageProducer = kafkaMessageProducer;
    this.topic = topic;
  }

  @PostPersist
  public void handleLearningOutcomeCreate(LearningOutcome learningOutcome)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        buildLearningOutcomeDomainEvent(learningOutcome, LearningOutcomeEventType.CREATED));
  }

  @PostUpdate
  public void handleLearningOutcomeSave(LearningOutcome learningOutcome)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        buildLearningOutcomeDomainEvent(learningOutcome, LearningOutcomeEventType.UPDATED));
  }

  @PostRemove
  public void handleLearningOutcomeDelete(LearningOutcome learningOutcome)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        buildLearningOutcomeDomainEvent(learningOutcome, LearningOutcomeEventType.DELETED));
  }

  private LearningOutcomeDomainEvent buildLearningOutcomeDomainEvent(
      LearningOutcome learningOutcome, LearningOutcomeEventType eventType) {
    return new LearningOutcomeDomainEvent(learningOutcome.getId(), eventType);
  }
}
