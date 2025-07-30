package io.github.joao_tinelli.libraryapi.validator;

import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.Livro;
import io.github.joao_tinelli.libraryapi.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LivroValidator {

    private final LivroRepository repository;

    public void validar(Livro livro) throws RegistroDuplicadoException{
        if (existeLivroIsbn(livro)){
            throw new RegistroDuplicadoException("ISBN ja cadastrado");
        }
    }

    private boolean existeLivroIsbn(Livro livro){
        Optional<Livro> livroEncontrado = repository.findByIsbn(livro.getIsbn());

        // Estamos tentando cadastrar um livro
        if (livro.getId() == null){
            // Neste caso, se a busca pelo ISBN encontrou qualquer livro (livroEncontrado não está vazio), significa que já existe um livro com esse ISBN, e o método retorna true (indicando uma duplicata).
            return livroEncontrado.isPresent();
        }

        // Atualizacao de um livro existente
        return livroEncontrado
                .map(Livro::getId) //  Se um livro foi encontrado pelo ISBN, pega o ID desse livro.
                .stream() // Converte o resultado para um fluxo (Stream) para poder usar o anyMatch.
                .anyMatch(id -> !id.equals(livro.getId())); // Verifica se o ID do livro encontrado no banco (id) é diferente do ID do livro que estamos tentando atualizar (livro.getId()).
                // O resultado será true (duplicado) somente se o banco de dados encontrou um livro com o mesmo ISBN, mas com um ID diferente. Se o ID for o mesmo, não é uma duplicata, é o próprio registro sendo atualizado.
    }
}
