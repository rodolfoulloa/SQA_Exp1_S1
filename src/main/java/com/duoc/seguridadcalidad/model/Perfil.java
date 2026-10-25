package com.duoc.seguridadcalidad.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Perfil gamer asociado a una cuenta (username de Spring Security).
 */
public class Perfil {

    private String cuenta;
    private String nombre;
    private String nombreUsuario;
    private String avatar = "avatar-1";
    private List<String> juegos = new ArrayList<>();
    private boolean notifCorreo = true;
    private boolean notifPush;
    private boolean notifNuevosEventos = true;

    public Perfil(String cuenta, String nombre, String nombreUsuario) {
        this.cuenta = cuenta;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
    }

    public String getCuenta() { return cuenta; }

    public String getNombre() { return nombre; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public List<String> getJuegos() { return juegos; }
    public void setJuegos(List<String> juegos) { this.juegos = juegos; }

    public boolean isNotifCorreo() { return notifCorreo; }
    public void setNotifCorreo(boolean notifCorreo) { this.notifCorreo = notifCorreo; }

    public boolean isNotifPush() { return notifPush; }
    public void setNotifPush(boolean notifPush) { this.notifPush = notifPush; }

    public boolean isNotifNuevosEventos() { return notifNuevosEventos; }
    public void setNotifNuevosEventos(boolean notifNuevosEventos) { this.notifNuevosEventos = notifNuevosEventos; }
}
