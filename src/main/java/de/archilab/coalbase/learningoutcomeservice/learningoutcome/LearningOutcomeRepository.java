package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LearningOutcomeRepository extends
    CrudRepository<LearningOutcome, LearningOutcomeIdentifier> {
}
