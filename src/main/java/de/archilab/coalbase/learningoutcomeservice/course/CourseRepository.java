package de.archilab.coalbase.learningoutcomeservice.course;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;

import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;

@RepositoryRestResource
public interface CourseRepository extends
    CrudRepository<Course, UniqueId<Course>> {

  @Override
  @PostFilter("filterObject.author == authentication.name")
  Iterable<Course> findAll();
}
