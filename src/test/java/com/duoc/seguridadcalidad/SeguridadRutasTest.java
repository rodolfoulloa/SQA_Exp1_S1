package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SeguridadRutasTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"/", "/home", "/login", "/registro", "/webjars/bootstrap/css/bootstrap.min.css", "/images/avatar-1.svg"})
    void rutasPublicasRespondenSinLogin(String ruta) throws Exception {
        mockMvc.perform(get(ruta)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/eventos", "/eventos/1", "/perfil"})
    void rutasPrivadasRedirigenALogin(String ruta) throws Exception {
        mockMvc.perform(get(ruta))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "gamer1")
    void usuarioAutenticadoVeEventosYDetalle() throws Exception {
        mockMvc.perform(get("/eventos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("evento(s) encontrado(s)")));
        mockMvc.perform(get("/eventos/1")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Servicios disponibles")));
        mockMvc.perform(get("/eventos").param("categoria", "eSports").param("ubicacion", "vina"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Torneo Rocket League")));
    }

    @Test
    @WithMockUser(username = "gamer1")
    void eventoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/eventos/9999")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "gamer2")
    void usuarioAutenticadoVeYEditaSuPerfil() throws Exception {
        mockMvc.perform(get("/perfil")).andExpect(status().isOk());
        mockMvc.perform(post("/perfil").with(csrf())
                        .param("nombreUsuario", "ProGamer_2")
                        .param("avatar", "avatar-3")
                        .param("juegos", "Valorant", "Tekken 8")
                        .param("notifCorreo", "true"))
                .andExpect(redirectedUrl("/perfil"));
        mockMvc.perform(get("/perfil"))
                .andExpect(content().string(containsString("ProGamer_2")))
                .andExpect(content().string(containsString("Tekken 8")));
    }

    @Test
    void usuariosPrecargadosPuedenIniciarSesion() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("Admin123!"))
                .andExpect(authenticated().withUsername("admin").withRoles("USER", "ADMIN"));
        mockMvc.perform(formLogin().user("gamer1").password("Gamer123!"))
                .andExpect(authenticated().withUsername("gamer1"));
        mockMvc.perform(formLogin().user("gamer2").password("Gamer123!"))
                .andExpect(authenticated().withUsername("gamer2"));
    }

    @Test
    void credencialesInvalidasNoAutentican() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("incorrecta"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void registroCreaUsuarioQuePuedeIniciarSesion() throws Exception {
        mockMvc.perform(post("/registro").with(csrf())
                        .param("nombre", "Nueva Jugadora")
                        .param("correo", "Nueva@Duoc.cl")
                        .param("password", "Clave1234")
                        .param("confirmarPassword", "Clave1234"))
                .andExpect(redirectedUrl("/login?registrado"));

        mockMvc.perform(formLogin().user("nueva@duoc.cl").password("Clave1234"))
                .andExpect(authenticated().withUsername("nueva@duoc.cl"));
    }

    @Test
    void registroConDatosInvalidosNoCreaUsuario() throws Exception {
        mockMvc.perform(post("/registro").with(csrf())
                        .param("nombre", "")
                        .param("correo", "no-es-correo")
                        .param("password", "corta")
                        .param("confirmarPassword", "otra"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ingresa un correo válido")));
    }

    @Test
    void registroSinTokenCsrfEsRechazado() throws Exception {
        mockMvc.perform(post("/registro")
                        .param("nombre", "Sin Token")
                        .param("correo", "sintoken@duoc.cl")
                        .param("password", "Clave1234")
                        .param("confirmarPassword", "Clave1234"))
                .andExpect(status().isForbidden());
    }

    @Test
    void respuestasIncluyenCabecerasDeSeguridad() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'self'")));
    }
}
