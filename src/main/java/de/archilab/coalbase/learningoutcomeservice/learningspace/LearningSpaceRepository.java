package de.archilab.coalbase.learningoutcomeservice.learningspace;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;

@RepositoryRestResource
@CrossOrigin("*")
public interface LearningSpaceRepository extends
    CrudRepository<LearningSpace, UniqueId<LearningSpace>> {

}
