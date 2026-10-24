package com.seguridad.practica.repositorio;
import com.seguridad.practica.entidades.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UsuarioRepo extends JpaRepository<Usuario, String> {
   Optional<Usuario> findByUsername(String username);
   Boolean existsByUsername(String username);
   Boolean existsByEmail(String email);
}
