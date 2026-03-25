package io.github.joao_tinelli.libraryapi.service;

import io.github.joao_tinelli.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.repository.AutorRepository;
import io.github.joao_tinelli.libraryapi.repository.LivroRepository;
import io.github.joao_tinelli.libraryapi.validator.AutorValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutorService {
    private final AutorRepository repository;
    private final AutorValidator validator;
    private final LivroRepository livroRepository;

    @Transactional
    public Autor salvar(Autor autor) throws RegistroDuplicadoException {
        validator.validar(autor);
        log.info("Salvando autor: {}", autor.getNome());
        return repository.save(autor);
    }

    @Transactional
    public void atualizar(Autor autor) throws RegistroDuplicadoException {
        if (autor.getId() == null){
            throw new IllegalArgumentException("Autor nao encontrado");
        }
        validator.validar(autor);
        log.info("Atualizando autor: id={}", autor.getId());
        repository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id){ return repository.findById(id); }

    @Transactional
    public void deletar(Autor autor) throws OperacaoNaoPermitidaException {
        if (possuiLivro(autor)){
            throw new OperacaoNaoPermitidaException("Não é permitido excluir um autor que possui livros cadastrados!");
        }
        log.info("Deletando autor: id={}", autor.getId());
        repository.delete(autor);
    }

    public List<Autor> pesquisa(String nome, String nacionalidade){
        if (nome != null && nacionalidade != null){
            return repository.findByNomeAndNacionalidade(nome, nacionalidade);
        }

        if (nome != null){
            return  repository.findByNome(nome);
        }

        if (nacionalidade != null){
            return  repository.findByNacionalidade(nacionalidade);
        }

        return repository.findAll();
    }

    public Page<Autor> pesquisaByExample(String nome, String nacionalidade, Integer pagina, Integer tamanhoPagina){
        var autor = new Autor();
        autor.setNome(nome);
        autor.setNacionalidade(nacionalidade);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Autor> autorExample = Example.of(autor, matcher);
        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(autorExample, pageRequest);
    }

    public boolean possuiLivro(Autor autor){
        return livroRepository.existsByAutor(autor);
    }
}
