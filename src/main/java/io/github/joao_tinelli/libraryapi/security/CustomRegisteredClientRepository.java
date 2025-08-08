package io.github.joao_tinelli.libraryapi.security;

import io.github.joao_tinelli.libraryapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

@Component //  transforma a classe em um "bean" gerenciado pelo Spring, permitindo que o Spring a encontre e a utilize automaticamente como a implementação oficial de RegisteredClientRepository
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientService clientService;
    private final TokenSettings tokenSettings;
    private final ClientSettings clientSettings;

    @Override
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        // 1. Busca o cliente no seu banco de dados
        var client = clientService.obterPorClientId(clientId);

        if (client == null){
            return null; // Informa ao Spring que o cliente não existe
        }

        // 2. Constrói o objeto que o Spring entende
        return RegisteredClient
                .withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret()) // A senha da aplicação cliente
                .redirectUri(client.getRedirectURI()) // Para o Authorization Code Flow
                .scope(client.getScope()) // As permissões (ex: "read", "write")

                // 3. Define os fluxos permitidos para este cliente
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)

                // 4. Aplica as configurações globais
                .tokenSettings(tokenSettings)
                .clientSettings(clientSettings)
                .build();
    }
}
