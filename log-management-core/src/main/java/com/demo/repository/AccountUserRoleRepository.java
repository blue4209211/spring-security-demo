package com.demo.repository;

import com.demo.repository.model.Account;
import com.demo.repository.model.AccountUserRole;
import com.demo.repository.model.Role;
import com.demo.security.DefaultSystemRolesEnum;
import com.demo.repository.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AccountUserRoleRepository extends JpaRepository<AccountUserRole, Long> {

    Set<AccountUserRole> findByRole(Role role);

    Set<AccountUserRole> findByAccountAndUser(Account account, User user);

    Set<AccountUserRole> findByAccountIdAndUserId(Long accountId, Long userId);

    Optional<AccountUserRole> findByAccountAndUserAndRole(Account account, User user, Role role);

    void deleteByAccount(Account account);

    void deleteByAccountAndUser(Account account, User User);

    void deleteByAccountAndUserAndRole(Account account, User User, Role role);
}
