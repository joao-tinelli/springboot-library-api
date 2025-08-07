package io.github.joao_tinelli.libraryapi.service;

import io.github.joao_tinelli.libraryapi.model.Client;
import io.github.joao_tinelli.libraryapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    public Client salvar(Client client){
        return repository.save(client);
    }

    public Client obterPorClientId(String clientId){
        return repository.findByClientId(clientId);
    }
}
