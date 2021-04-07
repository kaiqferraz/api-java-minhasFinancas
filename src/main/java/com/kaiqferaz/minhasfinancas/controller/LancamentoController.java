package com.kaiqferaz.minhasfinancas.controller;

import com.kaiqferaz.minhasfinancas.controller.dto.AtualizaStatusDTO;
import com.kaiqferaz.minhasfinancas.controller.dto.LancamentoDTO;
import com.kaiqferaz.minhasfinancas.exceptions.ErroAutenticacao;
import com.kaiqferaz.minhasfinancas.exceptions.RegraNegocioException;
import com.kaiqferaz.minhasfinancas.model.Lancamento;
import com.kaiqferaz.minhasfinancas.model.StatusLancamento;
import com.kaiqferaz.minhasfinancas.model.TipoLancamento;
import com.kaiqferaz.minhasfinancas.model.Usuario;
import com.kaiqferaz.minhasfinancas.service.LancamentoService;
import com.kaiqferaz.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    @Autowired
    private final LancamentoService lancamentoService;
    @Autowired
    private final UsuarioService usuarioService;




    ////////////////////------------BUSCAR------------////////////////////////

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value ="descricao" , required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario
    ) {

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
        }else {
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }




    ////////////////////------------SALVAR------------////////////////////////

    @PostMapping
    public ResponseEntity salvar (@RequestBody LancamentoDTO lancamentoDTO){
      try {
          Lancamento entidade = converter(lancamentoDTO);
          entidade = lancamentoService.salvar(entidade);
          return new ResponseEntity(entidade, HttpStatus.CREATED);
      }catch (RegraNegocioException e) {
          return ResponseEntity.badRequest().body(e.getMessage());
      }
    }



    ////////////////////------------EDITAR------------////////////////////////
    @PutMapping("{id}")
    public ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody LancamentoDTO dto ) {
        return lancamentoService.obterPorId(id).map( entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                lancamentoService.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            }catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () ->
                new ResponseEntity("Lancamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );
    }




    ////////////////////------------ATUALIZAR STATUS------------////////////////////////

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
        return lancamentoService.obterPorId(id).map(entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if (statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Não foi possivel atualizar o status do lançamento, envie um status valido!");
            }

            try{
                entity.setStatus(statusSelecionado);
                lancamentoService.atualizar(entity);
                return ResponseEntity.ok(entity);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }).orElseGet( () ->
                new ResponseEntity("Lancamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );


    }










    ////////////////////------------DELETAR------------////////////////////////

    @DeleteMapping("{id}")
    public ResponseEntity deletar( @PathVariable("id") Long id ) {
        return lancamentoService.obterPorId(id).map( entidade -> {
            lancamentoService.deletar(entidade);
            return new ResponseEntity( HttpStatus.NO_CONTENT );
        }).orElseGet( () ->
                new ResponseEntity("Lancamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );
    }


    ////////////////////------------VALIDAÇÕES------------////////////////////////

    private LancamentoDTO converter(Lancamento lancamento) {
        return LancamentoDTO.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .usuario(lancamento.getUsuario().getId())
                .build();

    }




    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow( () -> new RegraNegocioException("Usuário não encontrado para o Id informado.") );

        lancamento.setUsuario(usuario);

        if(dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        if(dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }
}
