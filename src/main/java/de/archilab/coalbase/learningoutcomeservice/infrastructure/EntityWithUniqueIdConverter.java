package de.archilab.coalbase.learningoutcomeservice.infrastructure;

import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class EntityWithUniqueIdConverter implements BackendIdConverter {

  @Override
  public Serializable fromRequestId(String id, Class<?> aClass) {
      return new UniqueId(id);
  }

  @Override
  public String toRequestId(Serializable serializable, Class<?> aClass) {
    return ((UniqueId) serializable).toIdString();
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return EntityWithUniqueId.class.isAssignableFrom(aClass);
  }
}
