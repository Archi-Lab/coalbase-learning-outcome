package de.archilab.coalbase.learningoutcomeservice.examform;

import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource
public interface PredefinedExamFormRepository extends
        CrudRepository<PredefinedExamForm, UniqueId<PredefinedExamForm>> {

    Optional<PredefinedExamForm> findByType(ExamType examType);

    @Override
    @RestResource(exported=false)
    <S extends PredefinedExamForm> S save(S s);

    @Override
    @RestResource(exported=false)
    <S extends PredefinedExamForm> Iterable<S> saveAll(Iterable<S> iterable);

    @Override
    @RestResource(exported=false)
    void delete(PredefinedExamForm predefinedExamForm);

    @Override
    @RestResource(exported=false)
    void deleteAll();

    @Override
    @RestResource(exported=false)
    void deleteAll(Iterable<? extends PredefinedExamForm> iterable);

    @Override
    @RestResource(exported=false)
    void deleteById(UniqueId<PredefinedExamForm> predefinedExamFormUniqueId);
}

