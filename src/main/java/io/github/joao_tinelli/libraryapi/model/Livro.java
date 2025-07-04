package io.github.joao_tinelli.libraryapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "livro")
@Data // ja inclui @Getter @Setter @ToString @RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Livro {

    // id uuid not null primary key
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // isbn varchar(20) not null
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    // titulo varchar(150) not null,
    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    // data_publicacao date not null
    @Column(name = "data_publicacao", nullable = false)
    private LocalDate dataPublicacao;

    // genero varchar(30) not null,
    @Enumerated(EnumType.STRING)
    @Column(name = "genero", length = 30, nullable = false)
    private GeneroLivro genero;

    // preco numeric(18, 2)
    @Column(name = "preco", precision = 18, scale = 2)
    private BigDecimal preco;

    // id_autor uuid not null references autor(id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor")
    private Autor autor;

    // data_cadastro timestamp
    @CreatedDate
    // Spring Data JPA vai preencher com a data de criacao para mim (necessario: @EnableJpaAuditing em Application e @EntityListeners(AuditingEntityListener.class) aqui)
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    // data_atualizacao timestamp
    @LastModifiedDate
    // Spring Data JPA vai preencher com a ultima data para mim (necessario: @EnableJpaAuditing em Application e @EntityListeners(AuditingEntityListener.class) aqui)
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // id_usuario uuid
    @Column(name = "id_usuario")
    private UUID idUsuario;
}
