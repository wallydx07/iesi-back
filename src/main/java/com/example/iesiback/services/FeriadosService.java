package com.example.iesiback.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FeriadosService {
    private static final String URL_FERIADOS = "https://api.argentinadatos.com/v1/feriados/";

    public Map<LocalDate, String> obtenerFeriadosConMotivo(int anio) {
        RestTemplate rest = new RestTemplate();
        String url = URL_FERIADOS + anio;
        List<Map<String, String>> lista = rest.getForObject(url, List.class);

        Map<LocalDate, String> mapa = new HashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Map<String, String> item : lista) {
            LocalDate fecha = LocalDate.parse(item.get("fecha"), fmt);
            String motivo = item.get("nombre");
            mapa.put(fecha, motivo);
        }

        return mapa;
    }
}