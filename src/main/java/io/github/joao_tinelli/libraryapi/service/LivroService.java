package io.github.joao_tinelli.libraryapi.service;

import io.github.joao_tinelli.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import io.github.joao_tinelli.libraryapi.model.Livro;
import io.github.joao_tinelli.libraryapi.repository.LivroRepository;
import io.github.joao_tinelli.libraryapi.repository.specs.LivroSpecs;
import io.github.joao_tinelli.libraryapi.validator.LivroValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LivroService {
    private final LivroRepository livroRepository;
    private final LivroValidator validator;

    @Transactional
    public Livro salvar(Livro livro) throws RegistroDuplicadoException {
        validator.validar(livro);
        log.info("Salvando livro: isbn={}, titulo={}", livro.getIsbn(), livro.getTitulo());
        return livroRepository.save(livro);
    }

    public Optional<Livro> obterPorId(UUID id){
        return livroRepository.findById(id);
    }

    @Transactional
    public void deletar(Livro livro) {
        log.info("Deletando livro: id={}", livro.getId());
        livroRepository.delete(livro);
    }

    public Page<Livro> pesquisa(String isbn, String titulo, String nomeAutor, GeneroLivro genero, Integer anoPublicacao, Integer pagina, Integer tamanhoPagina){

        // select * from livro where 0 = 0
        Specification<Livro> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if (isbn != null){
            specs = specs.and(LivroSpecs.isbnEqual(isbn));
        }

        if (titulo != null){
            specs = specs.and(LivroSpecs.tituloLike(titulo));
        }

        if (nomeAutor != null){
            specs = specs.and(LivroSpecs.nomeAutorLike(nomeAutor));
        }

        if (genero != null){
            specs = specs.and(LivroSpecs.generoEqual(genero));
        }

        if (anoPublicacao != null){
            specs = specs.and(LivroSpecs.anoPublicacaoEqual(anoPublicacao));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return livroRepository.findAll(specs, pageRequest);
    }

    @Transactional
    public void atualizar(Livro livro) throws IllegalArgumentException, RegistroDuplicadoException {
        if (livro.getId() == null){
            throw new IllegalArgumentException("Livro nao encontrado");
        }
        validator.validar(livro);
        log.info("Atualizando livro: id={}", livro.getId());
        livroRepository.save(livro);
    }
}
