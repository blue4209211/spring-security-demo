package com.demo.repository;

import com.demo.repository.model.Account;
import com.demo.repository.model.User;
import com.demo.repository.model.UserTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    List<User> findByIdIn(Set<Long> userIds);

    Set<User> findByRolesAccount(Account account);

    List<User> findByUserTypeAndRolesAccount(UserTypeEnum userType, Account account);

    Boolean existsByUserId(String email);

}
