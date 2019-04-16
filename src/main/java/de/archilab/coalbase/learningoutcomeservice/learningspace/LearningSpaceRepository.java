package de.archilab.coalbase.learningoutcomeservice.learningspace;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;

@RepositoryRestResource
public interface LearningSpaceRepository extends
    CrudRepository<LearningSpace, UniqueId<LearningSpace>> {

}
