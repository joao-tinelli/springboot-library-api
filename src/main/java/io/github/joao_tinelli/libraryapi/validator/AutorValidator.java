package io.github.joao_tinelli.libraryapi.validator;

import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.repository.AutorRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component // para que seja gerenciado pelo Spring e para que eu consiga injetá-lo na camada de serviço
public class AutorValidator {
    private AutorRepository autorRepository;

    // Injecao de dependencia
    public AutorValidator(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public void validar(Autor autor) throws RegistroDuplicadoException {
        if (existeAutorCadastrado(autor)){
            throw new RegistroDuplicadoException("Autor já cadastrado.");
        }
    }

    // Lógica para ver se já existe um autor cadastrado
    private boolean existeAutorCadastrado(Autor autor){
        Optional<Autor> autorEncontrado = autorRepository.findByNomeAndDataNascimentoAndNacionalidade(autor.getNome(),
                autor.getDataNascimento(), autor.getNacionalidade());

        // Se for um autor novo
        if (autor.getId() == null){
            return autorEncontrado.isPresent();
        }
        return !autor.getId().equals(autorEncontrado.get().getId()) && autorEncontrado.isPresent();
    }

}
