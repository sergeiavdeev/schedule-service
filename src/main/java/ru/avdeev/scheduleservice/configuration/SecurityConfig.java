package ru.avdeev.scheduleservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http.authorizeExchange((authorize) -> authorize
                        .pathMatchers(HttpMethod.GET, "/v1/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/v1/calendar/**").hasAnyRole("CALENDAR", "ADMIN")
                        .anyExchange().permitAll()
                )
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(
                                source -> Mono.just(jwtAuthenticationConverter().convert(source)))
                        )
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("preferred_username");
        converter.setJwtGrantedAuthoritiesConverter(jwt -> jwt.getClaimAsStringList("spring_roles").stream()
                .map(String::toUpperCase)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList()
        );
        return converter;
    }
}
