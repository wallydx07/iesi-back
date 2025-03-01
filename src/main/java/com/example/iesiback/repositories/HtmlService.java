package com.example.iesiback.repositories;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

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
}
