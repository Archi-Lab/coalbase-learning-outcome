package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface LearningOutcomeRepository extends
    PagingAndSortingRepository<LearningOutcome, LearningOutcomeIdentifier> {
}
