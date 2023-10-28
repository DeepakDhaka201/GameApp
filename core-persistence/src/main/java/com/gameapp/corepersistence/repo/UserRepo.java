package com.gameapp.corepersistence.repo;

import com.gameapp.corepersistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    User findByPhone(String phone);
}