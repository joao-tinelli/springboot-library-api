package io.github.joao_tinelli.libraryapi.config;

import io.github.joao_tinelli.libraryapi.security.JwtCustomAuthenticationFilter;
import io.github.joao_tinelli.libraryapi.security.LoginSocialSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginSocialSuccessHandler successHandler, JwtCustomAuthenticationFilter jwtCustomAuthenticationFilter) throws Exception {
        return http
                // 1. Desativa CSRF (para APIs REST)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. HTTP Basic Auth (útil para testes em ferramentas como Postman)
                .httpBasic(Customizer.withDefaults())

                // 3. Login por formulário
                .formLogin(configurer -> configurer
                        .loginPage("/login")
                        .permitAll()
                )

                // 4. Autorização de rotas
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/usuarios/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 5. Login social (Google, GitHub etc.)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(successHandler)
                )

                // 6. Resource Server (JWT)
                .oauth2ResourceServer(oauth2RS -> oauth2RS
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )

                // 7. Filtro para converter JwtToken em CustomAuthentication
                .addFilterAfter(jwtCustomAuthenticationFilter, BearerTokenAuthenticationFilter.class)

                .build();
    }

    /**
     * Conversor de claims do JWT para Authorities (roles)
     * Remove o prefixo "SCOPE_" e mantém apenas o nome do escopo/role.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_"); // Remove "SCOPE_"
        authoritiesConverter.setAuthoritiesClaimName("scope"); // ou "scp", dependendo do token

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }
}
