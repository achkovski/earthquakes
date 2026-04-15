package com.kirilachkovski.earthquakes.repository;

import com.kirilachkovski.earthquakes.entity.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EarthquakeRepository extends JpaRepository<Earthquake, Long>,
        JpaSpecificationExecutor<Earthquake> {

    @Query("SELECT e.externalId FROM Earthquake e")
    Set<String> findAllExternalIds();
}
