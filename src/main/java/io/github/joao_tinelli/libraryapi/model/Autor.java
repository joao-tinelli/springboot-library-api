package io.github.joao_tinelli.libraryapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "autor") // nome da tabela no PostgresSQL
@Getter
@Setter
@ToString(exclude = "livros")
@EntityListeners(AuditingEntityListener.class)
public class Autor {

    // id uuid not null primary key,
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID) // id vai ser gerado automaticamente
    private UUID id;

    // nome varchar(100) not null,
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    // data_nascimento date not null
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    // nacionalidade varchar(50) not null
    @Column(name = "nacionalidade", length = 50, nullable = false)
    private String nacionalidade;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Livro> livros;

    // data_cadastro timestamp
    @CreatedDate // Spring Data JPA vai preencher com a data de criacao para mim (necessario: @EnableJpaAuditing em Application e @EntityListeners(AuditingEntityListener.class) aqui)
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    // data_atualizacao timestamp
    @LastModifiedDate // Spring Data JPA vai preencher com a ultima data para mim (necessario: @EnableJpaAuditing em Application e @EntityListeners(AuditingEntityListener.class) aqui)
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // id_usuario uuid
    @Column(name = "id_usuario")
    private UUID idUsuario;

    @Deprecated
    public Autor(){
        // para uso do framework
    }
}
