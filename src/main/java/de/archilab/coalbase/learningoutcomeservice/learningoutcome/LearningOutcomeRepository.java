package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LearningOutcomeRepository extends
    CrudRepository<LearningOutcome, UniqueId<LearningOutcome>> {

}
