package com.example.iesiback.repositories;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class HtmlService {
    private final TemplateEngine templateEngine;

    public HtmlService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String  procesarHtml(String templateName, Map<String, Object> datos) {
        TemplateEngine textTemplateEngine = new TemplateEngine();
        StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
        Context context = new Context();
        context.setVariables(datos);
        return templateEngine.process(templateName, context);
    }


    public String procesarHtmlPlano(String templateName, Map<String, Object> datos) throws IOException {
        // Ruta absoluta al archivo HTML
        String ruta = "src/main/resources/templates/" + templateName + ".html";

        // Leer el contenido del archivo como String
        String html = new String(Files.readAllBytes(Paths.get(ruta)), StandardCharsets.UTF_8);

        // Reemplazar las variables ${...} por los valores del mapa
        return new StringSubstitutor(datos).replace(html);
    }
}
