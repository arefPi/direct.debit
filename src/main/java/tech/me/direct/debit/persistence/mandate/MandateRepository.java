package tech.me.direct.debit.persistence.mandate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MandateRepository extends CrudRepository<Mandate, Integer> {
    Optional<Mandate> findByReferenceId(String referenceId);
}
