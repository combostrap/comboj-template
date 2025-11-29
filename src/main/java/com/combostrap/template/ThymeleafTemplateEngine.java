package com.combostrap.template;

import com.combostrap.template.api.TemplateEngine;

public class ThymeleafTemplateEngine implements TemplateEngine {


  private static ThymeleafTemplateEngine staticTemplateEngine;
  private final org.thymeleaf.TemplateEngine templateEngine;

  private ThymeleafTemplateEngine(org.thymeleaf.TemplateEngine templateEngine) {

    this.templateEngine = templateEngine;

  }

  public static ThymeleafTemplateEngine getOrCreate(){
    if(staticTemplateEngine != null){
      return staticTemplateEngine;
    }

    org.thymeleaf.TemplateEngine templateEngineLocal = new org.thymeleaf.TemplateEngine();
    staticTemplateEngine = new ThymeleafTemplateEngine(templateEngineLocal);
    return staticTemplateEngine;
  }

  public static ThymeleafTemplateEngine createFromThymeleafEngine(org.thymeleaf.TemplateEngine thymeleafTemplateEngine) {
    return new ThymeleafTemplateEngine(thymeleafTemplateEngine);
  }

  @Override
  public ThymeleafTemplate compile(String string) {
    return new ThymeleafTemplate(string, templateEngine);
  }

  @Override
  public void clearCache() {
    this.templateEngine.clearTemplateCache();
  }



}
