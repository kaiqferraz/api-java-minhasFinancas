package com.kaiqferaz.minhasfinancas.repository;

import com.kaiqferaz.minhasfinancas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //METODO USADO NA CLASSE UsuarioServiceImpl p/ consultar email no banco de dados
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

}
