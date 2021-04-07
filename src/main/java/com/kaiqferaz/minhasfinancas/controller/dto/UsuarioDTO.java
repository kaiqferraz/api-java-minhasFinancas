package com.kaiqferaz.minhasfinancas.controller.dto;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private String email;

    private String nome;

    private String senha;



}
