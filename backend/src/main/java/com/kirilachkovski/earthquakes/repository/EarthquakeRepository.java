package com.kirilachkovski.earthquakes.repository;

import com.kirilachkovski.earthquakes.entity.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarthquakeRepository extends JpaRepository<Earthquake, Long> {
}
