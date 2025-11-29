package com.combostrap.template;

import com.combostrap.template.api.Template;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ThymeleafTemplate implements Template {


  private final String templateString;

  private final Map<String, Object> params = new HashMap<>();
  private final TemplateEngine templateEngine;

  public ThymeleafTemplate(String template, org.thymeleaf.TemplateEngine templateEngine) {
    this.templateString = template;
    this.templateEngine = templateEngine;
  }


  @Override
  public ThymeleafTemplate applyVariables(Map<String, Object> params) {
    this.params.putAll(params);
    return this;
  }

  @Override
  public String getResult() {
    /**
     * A Thymeleaf context is an object implementing the org.thymeleaf.context.IContext interface.
     * Contexts should contain all the data required for an execution of the template engine in a variables map,
     * and also reference the locale that must be used for externalized messages.
     */
    IContext context = new Context(Locale.US, this.params);
    return templateEngine.process(this.templateString, context);
  }

}
