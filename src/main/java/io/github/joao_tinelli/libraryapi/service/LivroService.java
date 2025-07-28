package io.github.joao_tinelli.libraryapi.service;

import io.github.joao_tinelli.libraryapi.model.Livro;
import io.github.joao_tinelli.libraryapi.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LivroService {
    private final LivroRepository livroRepository;

    public Livro salvar(Livro livro) {
        return livroRepository.save(livro);
    }
}
