package io.archilab.coalbase.learningoutcomeservice.course;

import io.archilab.coalbase.learningoutcomeservice.core.UniqueId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;

@RepositoryRestResource
public interface CourseRepository extends
    CrudRepository<Course, UniqueId<Course>> {

  @Override
  @PostFilter("filterObject.author == authentication.name")
  Iterable<Course> findAll();
}
