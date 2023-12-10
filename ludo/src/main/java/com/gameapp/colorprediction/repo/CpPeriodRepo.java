package com.gameapp.colorprediction.repo;


import com.gameapp.colorprediction.entity.CpPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CpPeriodRepo extends JpaRepository<CpPeriod, Long> {
    CpPeriod save(CpPeriod cpPeriod);

    CpPeriod findFirstByOrderByIdDesc();

    Optional<CpPeriod> findById(Long id);

    @Query(value = "SELECT * FROM cp_period ORDER BY id DESC LIMIT 20", nativeQuery = true)
    List<CpPeriod> find20ByOrderByIdDesc();

    @Query(value = "SELECT * FROM cp_period ORDER BY id DESC LIMIT 200", nativeQuery = true)
    List<CpPeriod> find200ByOrderByIdDesc();
}
