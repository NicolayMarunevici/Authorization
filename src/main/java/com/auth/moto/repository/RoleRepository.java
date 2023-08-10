package com.auth.moto.repository;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
