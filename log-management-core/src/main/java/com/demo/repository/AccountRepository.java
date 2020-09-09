package com.demo.repository;

import com.demo.repository.model.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByNameIgnoreCase(String name);

    Optional<Account> findByNameIgnoreCase(String name);
}
