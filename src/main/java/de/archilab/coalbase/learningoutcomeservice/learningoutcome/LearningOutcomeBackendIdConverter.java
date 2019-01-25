package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
public class LearningOutcomeBackendIdConverter implements BackendIdConverter {

  @Override
  public Serializable fromRequestId(String id, Class<?> entityType) {
    final UUID uuid = UUID.fromString(id);
    return new LearningOutcomeIdentifier(uuid);
  }

  @Override
  public String toRequestId(Serializable source, Class<?> entityType) {
    LearningOutcomeIdentifier id = (LearningOutcomeIdentifier) source;
    return id.toString();
  }

  @Override
  public boolean supports(Class<?> type) {
    return LearningOutcome.class.equals(type);
  }
}
