package com.duoc.seguridadcalidad.service;

import com.duoc.seguridadcalidad.model.Perfil;
import com.duoc.seguridadcalidad.model.PerfilForm;
import com.duoc.seguridadcalidad.model.RegistroForm;
import com.duoc.seguridadcalidad.repository.PerfilRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Registro de usuarios y gestion de perfiles.
 */
@Service
public class UsuarioService {

    public static final List<String> JUEGOS = List.of(
            "League of Legends", "Valorant", "Counter-Strike 2", "Fortnite", "Minecraft",
            "Rocket League", "Street Fighter 6", "Tekken 8", "EA Sports FC", "Genshin Impact",
            "Pokémon", "Super Smash Bros.");

    public static final List<String> AVATARES = List.of("avatar-1", "avatar-2", "avatar-3", "avatar-4");

    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final PerfilRepository perfilRepository;

    public UsuarioService(InMemoryUserDetailsManager userDetailsManager,
                          PasswordEncoder passwordEncoder,
                          PerfilRepository perfilRepository) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.perfilRepository = perfilRepository;
    }

    /**
     * Crea la cuenta (el correo es el nombre de login) con la contrasena cifrada en BCrypt.
     *
     * @throws IllegalArgumentException si el correo ya esta registrado
     */
    public void registrar(RegistroForm form) {
        String correo = form.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (userDetailsManager.userExists(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        userDetailsManager.createUser(User.withUsername(correo)
                .password(passwordEncoder.encode(form.getPassword()))
                .roles("USER")
                .build());

        String alias = correo.substring(0, correo.indexOf('@')).replaceAll("[^A-Za-z0-9_]", "_");
        perfilRepository.save(new Perfil(correo, form.getNombre().trim(), alias));
    }

    /** Devuelve el perfil de la cuenta; si no existe (usuarios precargados) lo crea. */
    public Perfil obtenerPerfil(String cuenta) {
        return perfilRepository.findByCuenta(cuenta)
                .orElseGet(() -> perfilRepository.save(new Perfil(cuenta, cuenta, cuenta)));
    }

    public boolean nombreUsuarioDisponible(String nombreUsuario, String cuenta) {
        return !perfilRepository.existsNombreUsuarioEnOtraCuenta(nombreUsuario, cuenta);
    }

    public Perfil actualizarPerfil(String cuenta, PerfilForm form) {
        Perfil perfil = obtenerPerfil(cuenta);
        perfil.setNombreUsuario(form.getNombreUsuario().trim());
        // Solo se aceptan valores de las listas permitidas (lista blanca).
        String avatar = form.getAvatar();
        perfil.setAvatar(avatar != null && AVATARES.contains(avatar) ? avatar : AVATARES.get(0));
        List<String> juegos = new ArrayList<>();
        if (form.getJuegos() != null) {
            form.getJuegos().stream().filter(JUEGOS::contains).distinct().forEach(juegos::add);
        }
        perfil.setJuegos(juegos);
        perfil.setNotifCorreo(form.isNotifCorreo());
        perfil.setNotifPush(form.isNotifPush());
        perfil.setNotifNuevosEventos(form.isNotifNuevosEventos());
        return perfilRepository.save(perfil);
    }
}
