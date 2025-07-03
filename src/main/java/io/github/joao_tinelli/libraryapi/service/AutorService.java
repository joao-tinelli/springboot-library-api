package io.github.joao_tinelli.libraryapi.service;

import io.github.joao_tinelli.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.repository.AutorRepository;
import io.github.joao_tinelli.libraryapi.repository.LivroRepository;
import io.github.joao_tinelli.libraryapi.validator.AutorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor // evita a necessidade de inserir um construtor (em tempo de compilacao, o lombok vai adicionar o construtor para mim)
public class AutorService {
    private final AutorRepository repository;
    private final AutorValidator validator;
    private final LivroRepository livroRepository;

    public Autor salvar(Autor autor) throws RegistroDuplicadoException {
        validator.validar(autor); // <------
        return repository.save(autor);
    }

    public void atualizar(Autor autor) throws RegistroDuplicadoException {
        if (autor.getId() == null){
            throw new IllegalArgumentException("Autor nao encontrado");
        }
        validator.validar(autor); // <------
        repository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id){ return repository.findById(id); }

    public void deletar(Autor autor) throws OperacaoNaoPermitidaException {
        if (possuiLivro(autor)){
            throw new OperacaoNaoPermitidaException("Não é permitido excluir um autor que possui livros cadastrados!");
        }
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

    public boolean possuiLivro(Autor autor){
        return livroRepository.existsByAutor(autor);
    }
}
