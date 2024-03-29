package mostwanted.repository;

import mostwanted.domain.entities.Racer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RacerRepository extends JpaRepository<Racer, Integer> {

    Optional<Racer> findByName(String racerName);

    @Query("" +
            "SELECT r " +
            "FROM mostwanted.domain.entities.Racer r " +
            "JOIN r.cars c " +
            "GROUP BY r.id " +
            "ORDER BY size(c.id) DESC, r.name")
    List<Racer> exportRacingCars();

}
