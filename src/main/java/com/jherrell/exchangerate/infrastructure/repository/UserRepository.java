package com.jherrell.exchangerate.infrastructure.repository;

import com.jherrell.exchangerate.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

}
