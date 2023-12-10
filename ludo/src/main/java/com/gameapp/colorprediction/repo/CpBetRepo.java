package com.gameapp.colorprediction.repo;

import com.gameapp.colorprediction.entity.CpBet;
import com.gameapp.core.dto.CPBetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CpBetRepo extends JpaRepository<CpBet, Long> {
    Optional<CpBet> findByUserIdAndPeriodId(String userId, Long periodId);

    CpBet save(CpBet cpBet);

    Optional<CpBet> findById(Long id);

    @Modifying
    @Query("UPDATE CpBet SET status = ?2 WHERE id = ?1")
    void updateStatus(Long id, CPBetStatus status);

    List<CpBet> findAllByPeriodIdAndStatus(Long id, CPBetStatus cpBetStatus);
}
