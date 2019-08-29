package io.archilab.coalbase.learningoutcomeservice.learningspace;

import io.archilab.coalbase.learningoutcomeservice.core.UniqueId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LearningSpaceRepository extends
    CrudRepository<LearningSpace, UniqueId<LearningSpace>> {

}
