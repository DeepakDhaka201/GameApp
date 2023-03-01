package com.example.ludo.service.repo;


import com.example.ludo.service.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceRepo extends JpaRepository<UserBalance, String> {
    UserBalance findByUserId(@Param("phone") String phone);

    @Modifying
    @Query("UPDATE UserBalance SET balance = balance - ?2, onHold = onHold + ?2 WHERE userId = ?1")
    void deductBalanceAndUpdateOnHold(String userId, Double amount);
}
