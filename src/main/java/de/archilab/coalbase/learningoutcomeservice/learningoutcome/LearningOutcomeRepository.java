package de.archilab.coalbase.learningoutcomeservice.learningoutcome;


import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface LearningOutcomeRepository extends
    PagingAndSortingRepository<LearningOutcome, UUID> {

}
