package com.duoc.seguridadcalidad.repository;

import com.duoc.seguridadcalidad.model.Perfil;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio en memoria de perfiles, indexado por la cuenta (username).
 */
@Repository
public class PerfilRepository {

    private final Map<String, Perfil> perfiles = new ConcurrentHashMap<>();

    public Optional<Perfil> findByCuenta(String cuenta) {
        return Optional.ofNullable(perfiles.get(cuenta));
    }

    public Perfil save(Perfil perfil) {
        perfiles.put(perfil.getCuenta(), perfil);
        return perfil;
    }

    public boolean existsNombreUsuarioEnOtraCuenta(String nombreUsuario, String cuenta) {
        return perfiles.values().stream()
                .anyMatch(p -> !p.getCuenta().equals(cuenta)
                        && p.getNombreUsuario() != null
                        && p.getNombreUsuario().equalsIgnoreCase(nombreUsuario));
    }
}
