package com.sklep.sklep_backend.repository;


import com.sklep.sklep_backend.entity.OurUsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsersEntity, Integer> {

    Optional<OurUsersEntity> findByEmail(String email);

}