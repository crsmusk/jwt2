package com.seguridad.practica.repositorio;

import com.seguridad.practica.entidades.Role;
import com.seguridad.practica.entidades.roles;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepo  extends JpaRepository<Role, String> {
  
   Optional<Role> findByRol(roles rol);

    boolean existsByRol(roles rol);
}
