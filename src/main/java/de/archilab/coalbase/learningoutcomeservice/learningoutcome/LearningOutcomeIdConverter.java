package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
public class LearningOutcomeIdConverter implements BackendIdConverter {

  @Override
  public Serializable fromRequestId(String id, Class<?> entityType) {
    if (id == null) {
      return null;
    }
    if (entityType.equals(LearningOutcome.class)) {
      final UUID uuid = UUID.fromString(id);
      return new LearningOutcomeIdentifier(uuid);
    }
    return DefaultIdConverter.INSTANCE.fromRequestId(id, entityType);
  }

  @Override
  public String toRequestId(Serializable source, Class<?> entityType) {

    if (entityType.equals(LearningOutcome.class)) {
      LearningOutcomeIdentifier id = (LearningOutcomeIdentifier) source;
      return id.toString();
    }

    return DefaultIdConverter.INSTANCE.toRequestId(source, entityType);
  }

  @Override
  public boolean supports(Class<?> type) {
    return LearningOutcome.class.equals(type);
  }
}
