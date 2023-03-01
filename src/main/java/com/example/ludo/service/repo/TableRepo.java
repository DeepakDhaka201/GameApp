package com.example.ludo.service.repo;

import com.example.ludo.dto.TableStatus;
import com.example.ludo.dto.UserGameStatus;
import com.example.ludo.service.entity.LudoTable;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepo extends JpaRepository<LudoTable, Long> {
    @Modifying
    @Query("UPDATE LudoTable SET status = ?2 WHERE id = ?1")
    void updateTableStatus(Long tableId, TableStatus status);

    @Modifying
    @Query(
            value = "UPDATE LudoTable SET roomCode = ?2, status = ?3," +
                    "acceptorBalanceDeducted = ?4, creatorBalanceDeducted = ?4, requestAcceptedAt = ?5 " +
                    "WHERE id = ?1"
    )
    void updateTableOnAccept(Long id, String roomCode, TableStatus running, Double amount, Long acceptedAt);

    @Modifying
    @Query("UPDATE LudoTable SET status = ?2, acceptedBy = ?3 WHERE id = ?1")
    void updateTableOnAcceptRequest(Long tableId, TableStatus matching, String acceptorId);

    @Modifying
    @Query("UPDATE LudoTable SET status = ?2, acceptedBy = ?3 WHERE id = ?1")
    void updateTableOnReject(Long tableId, TableStatus aNew, String acceptorId);

    @Modifying
    @Query("UPDATE LudoTable SET status = ?5, acceptorScreenshot = ?2, acceptorResult = ?3," +
            "acceptorReason = ?4 WHERE id = ?1")
    void updateTableOnAcceptorResult(Long id, String acceptorScreenshot, UserGameStatus acceptorStatus,
                                    String acceptorReason, TableStatus underReview);

    @Modifying
    @Query("UPDATE LudoTable SET status = ?5, creatorScreenshot = ?2, creatorResult = ?3," +
            "creatorReason = ?4 WHERE id = ?1")
    void updateTableOnCreatorResult(Long id, String creatorScreenshot, UserGameStatus creatorStatus,
                                    String creatorReason, TableStatus underReview);

    @Query(
            value = "SELECT * FROM ludo_table WHERE created_by = :userId OR accepted_by = :userId " +
                    "AND status IN ('RUNNING', 'COMPLETED', 'UNDER_REVIEW', 'CANCELLED')",
            nativeQuery = true)
    public List<LudoTable> findByCreatorIdOrAcceptorIdAndStatus(
            @Param("userId") String userId);

    @Query(value = "SELECT * FROM ludo_table WHERE status IN ('NEW', 'RUNNING', 'MATCHING')", nativeQuery = true)
    List<LudoTable> findAllByStatus();
}
