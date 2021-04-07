package com.kaiqferaz.minhasfinancas.controller;



import com.kaiqferaz.minhasfinancas.controller.dto.UsuarioDTO;
import com.kaiqferaz.minhasfinancas.exceptions.ErroAutenticacao;
import com.kaiqferaz.minhasfinancas.model.Usuario;
import com.kaiqferaz.minhasfinancas.service.LancamentoService;
import com.kaiqferaz.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;


@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    @Autowired
    private final UsuarioService usuarioService;

    @Autowired
    private final LancamentoService lancamentoService;


    @PostMapping("/autenticar")
    public ResponseEntity autenticar (@RequestBody UsuarioDTO usuarioDTO){

            try {
                Usuario usuarioAutenticado = usuarioService.autenticar(usuarioDTO.getEmail(), usuarioDTO.getSenha());
                return ResponseEntity.ok(usuarioAutenticado);
            }catch (ErroAutenticacao e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



   @PostMapping
   public ResponseEntity salvar(
           @RequestBody UsuarioDTO usuarioDTO) {

       Usuario usuario = Usuario.builder()
               .nome(usuarioDTO.getNome())
               .email(usuarioDTO.getEmail())
               .senha(usuarioDTO.getSenha())
               .build();

       try{
           Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
           return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
       }catch(Exception e){
           return ResponseEntity.badRequest().body(e.getMessage());
       }


   }



    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo( @PathVariable("id") Long id ) {
        Optional<Usuario> usuario = usuarioService.obterPorId(id);

        if(!usuario.isPresent()) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }



}
