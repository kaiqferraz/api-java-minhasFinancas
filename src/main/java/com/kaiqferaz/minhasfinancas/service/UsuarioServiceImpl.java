package com.kaiqferaz.minhasfinancas.service;

import com.kaiqferaz.minhasfinancas.exceptions.ErroAutenticacao;
import com.kaiqferaz.minhasfinancas.exceptions.RegraNegocioException;
import com.kaiqferaz.minhasfinancas.model.Usuario;
import com.kaiqferaz.minhasfinancas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super();
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        if(!usuario.isPresent()){
            throw  new ErroAutenticacao("Usuário não encontrado para o e-mail informado.");
        }
        if(!usuario.get().getSenha().equals(senha)){
            throw new ErroAutenticacao("Senha Inválida");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
       boolean existe = usuarioRepository.existsByEmail(email);
       if(existe){
           throw new RegraNegocioException("Já existe um usuário cadastrado com este email");
       }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}
