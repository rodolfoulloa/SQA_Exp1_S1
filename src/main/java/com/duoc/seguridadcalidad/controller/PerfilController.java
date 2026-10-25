package com.duoc.seguridadcalidad.controller;

import com.duoc.seguridadcalidad.model.Perfil;
import com.duoc.seguridadcalidad.model.PerfilForm;
import com.duoc.seguridadcalidad.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;

    public PerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute
    public void datosComunes(Model model) {
        model.addAttribute("juegosDisponibles", UsuarioService.JUEGOS);
        model.addAttribute("avatares", UsuarioService.AVATARES);
    }

    @GetMapping
    public String ver(Authentication auth, Model model) {
        Perfil perfil = usuarioService.obtenerPerfil(auth.getName());
        model.addAttribute("perfil", perfil);
        model.addAttribute("perfilForm", PerfilForm.desde(perfil));
        return "perfil";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("perfilForm") PerfilForm form,
                          BindingResult result,
                          Authentication auth,
                          Model model,
                          RedirectAttributes redirect) {
        String cuenta = auth.getName();
        if (!result.hasFieldErrors("nombreUsuario")
                && !usuarioService.nombreUsuarioDisponible(form.getNombreUsuario(), cuenta)) {
            result.rejectValue("nombreUsuario", "duplicado", "Ese nombre de usuario ya está en uso");
        }
        if (result.hasErrors()) {
            model.addAttribute("perfil", usuarioService.obtenerPerfil(cuenta));
            return "perfil";
        }
        usuarioService.actualizarPerfil(cuenta, form);
        redirect.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
        return "redirect:/perfil";
    }
}
