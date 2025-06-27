package io.github.joao_tinelli.libraryapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "livro")
@Data // ja inclui @Getter @Setter @ToString @RequiredArgsConstructor
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
}
