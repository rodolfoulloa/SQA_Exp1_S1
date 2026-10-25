package com.duoc.seguridadcalidad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Configuracion de Spring Security.
 * Rutas publicas: inicio, registro, login y recursos estaticos.
 * Todo lo demas (eventos, perfil) requiere autenticacion.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] RUTAS_PUBLICAS = {
            "/", "/home", "/registro", "/login", "/error",
            "/images/**", "/webjars/**"
    };

    private static final String CSP =
            "default-src 'self'; "
            + "img-src 'self' data:; "
            + "style-src 'self'; "
            + "script-src 'self'; "
            + "font-src 'self'; "
            + "form-action 'self'; "
            + "frame-ancestors 'none'; "
            + "base-uri 'self'; "
            + "object-src 'none'";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(RUTAS_PUBLICAS).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", false)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // CSRF queda activado por defecto; Thymeleaf agrega el token en cada th:action.
            .csrf(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.changeSessionId())
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentSecurityPolicy(csp -> csp.policyDirectives(CSP))
                .referrerPolicy(ref -> ref.policy(
                        ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            );

        return http.build();
    }

    /**
     * Usuarios en memoria (no hay base de datos en esta etapa).
     * Las contrasenas se guardan cifradas con BCrypt.
     * Los usuarios que se registran desde /registro se agregan a este mismo gestor.
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin")
                .password(encoder.encode("Admin123!"))
                .roles("USER", "ADMIN")
                .build());
        manager.createUser(User.withUsername("gamer1")
                .password(encoder.encode("Gamer123!"))
                .roles("USER")
                .build());
        manager.createUser(User.withUsername("gamer2")
                .password(encoder.encode("Gamer123!"))
                .roles("USER")
                .build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
