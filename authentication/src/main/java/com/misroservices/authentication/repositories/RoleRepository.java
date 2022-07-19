package com.misroservices.authentication.repositories;

import com.misroservices.authentication.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
