package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

import javax.annotation.security.PermitAll;

public interface LearningOutcomeRepository extends
    PagingAndSortingRepository<LearningOutcome, LearningOutcomeIdentifier> {

//  PagingAndSortingRepository Methods
  @Override
  @PermitAll
// No auth needed, needs to be whitelisted in securityconfig
  Iterable<LearningOutcome> findAll(Sort var1);

  @Override
  @PermitAll // No auth needed, needs to be whitelisted in securityconfig
  Page<LearningOutcome> findAll(Pageable var1);

//  CrudRepository Methods
  @Override
  @PreAuthorize("hasAnyRole('coalbase_professor', 'coalbase_admin')")
  <S extends LearningOutcome> S save(S var1);

  @Override
  @PreAuthorize("hasAnyRole('coalbase_professor', 'coalbase_admin')")
  <S extends LearningOutcome> Iterable<S> saveAll(Iterable<S> var1);

  @Override
  @PermitAll // No auth needed, needs to be whitelisted in securityconfig
  Optional<LearningOutcome> findById(LearningOutcomeIdentifier var1);

  @Override
  @PermitAll // No auth needed, needs to be whitelisted in securityconfig
  boolean existsById(LearningOutcomeIdentifier var1);

  @Override
  @PermitAll // No auth needed, needs to be whitelisted in securityconfig
  Iterable<LearningOutcome> findAll();

  @Override
  @PermitAll // No auth needed, needs to be whitelisted in securityconfig
  Iterable<LearningOutcome> findAllById(Iterable<LearningOutcomeIdentifier> var1);

  @Override // No auth needed, needs to be whitelisted in securityconfig
  long count();

  @Override
  @PreAuthorize("hasAnyRole('coalbase_professor', 'coalbase_admin')")
  void deleteById(LearningOutcomeIdentifier var1);

  @Override
  @PreAuthorize("hasAnyRole('coalbase_professor', 'coalbase_admin')")
  void delete(LearningOutcome var1);

  @Override
  @PreAuthorize("hasAnyRole('coalbase_professor', 'coalbase_admin')")
  void deleteAll(Iterable<? extends LearningOutcome> var1);

  @PreAuthorize("hasRole('coalbase_admin')")
  @Override
  void deleteAll();
}
