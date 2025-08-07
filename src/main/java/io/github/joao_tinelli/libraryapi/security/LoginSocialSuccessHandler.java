package io.github.joao_tinelli.libraryapi.security;

import io.github.joao_tinelli.libraryapi.model.Usuario;
import io.github.joao_tinelli.libraryapi.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginSocialSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UsuarioService usuarioService;
    private final PasswordEncoder encoder;
    private static final String SENHA_PADRAO = "123";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        // Tenta encontrar o usuário pelo e-mail
        Usuario usuario = usuarioService.obterPorEmail(email);

        if (usuario == null){
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setLogin(email);
            usuario.setSenha(SENHA_PADRAO);
            usuario.setRoles(List.of("OPERADOR"));
            usuarioService.salvar(usuario);
        }

        authentication = new CustomAuthentication(usuario);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Redireciona o usuário usando a lógica da classe pai
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
