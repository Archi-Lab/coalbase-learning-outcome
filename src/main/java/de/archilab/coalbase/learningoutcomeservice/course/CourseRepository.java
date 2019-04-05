package de.archilab.coalbase.learningoutcomeservice.course;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;

@RepositoryRestResource
@CrossOrigin("*")
public interface CourseRepository extends
    CrudRepository<Course, UniqueId<Course>> {

  /*
  TODO add filter to findAll
  @PostFilter("filterObject.author == authentication.name")
  */

  @Override
  Iterable<Course> findAll();
}
