package io.archilab.coalbase.learningoutcomeservice.core;

import java.io.Serializable;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;

@Component
public class EntityWithUniqueIdConverter implements BackendIdConverter {

  @Override
  public Serializable fromRequestId(String id, Class<?> aClass) {
    return new UniqueId(id);
  }

  @Override
  public String toRequestId(Serializable serializable, Class<?> aClass) {
    return ((UniqueId) serializable).toString();
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return EntityWithUniqueId.class.isAssignableFrom(aClass);
  }
}
