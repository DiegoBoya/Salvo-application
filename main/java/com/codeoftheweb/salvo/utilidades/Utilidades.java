package com.codeoftheweb.salvo.utilidades;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

public class Utilidades {

    public static Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    //con este metodo vemos si tenemos logueado a un usuario, si lo hay, en authentication tenemos sus datos
    //si retorna true es porque no tenemos un User logeado
    public static boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}
