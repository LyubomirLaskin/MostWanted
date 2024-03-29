package mostwanted.repository;

import mostwanted.domain.entities.Town;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TownRepository extends JpaRepository<Town, Integer> {

    Optional<Town> findByName(String townName);

    @Query("" +
            "SELECT t " +
            "FROM mostwanted.domain.entities.Town t " +
            "JOIN t.racers r " +
            "GROUP BY t.id " +
            "ORDER BY size(r.id) DESC, t.name")
    List<Town> exportRacingTowns();

}
