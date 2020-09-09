package com.demo.repository;

import com.demo.repository.model.UserAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAttrRepository extends JpaRepository<UserAttr, Long> {
    List<UserAttr> findByNameIgnoreCase_AndUserId(String name, long userId);

    List<UserAttr> findByNameIgnoreCase_AndValue_AndUserId(String name, String value, long userId);
}
