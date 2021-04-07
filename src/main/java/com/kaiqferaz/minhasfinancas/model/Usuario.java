package com.kaiqferaz.minhasfinancas.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "usuario")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name="nome")
  private String nome;

  @Column(name="email")
  private String email;

  @Column(name="senha")
  private String senha;


}
