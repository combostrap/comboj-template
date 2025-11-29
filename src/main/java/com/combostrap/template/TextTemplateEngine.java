package com.combostrap.template;

import com.combostrap.template.api.TemplateEngine;

/**
 * A text template engine.
 * <p>
 * This is used in all sort of text to replace ${}
 * such as in path
 * <p>
 * Therefore, this is in the core
 * <p>
 * <p>
 * Inspiration for the function interface comes
 * from <a href="https://codereview.stackexchange.com/questions/102339/fastest-possible-text-template-for-repeated-use">...</a>
 */
public class TextTemplateEngine implements TemplateEngine {


  private static TextTemplateEngine templateEngine;

  private TextTemplateEngine() {

  }


  @SuppressWarnings("unused")
  public static TextTemplateEngine getOrCreate() {
    if (templateEngine != null) {
      return templateEngine;
    }
    templateEngine = new TextTemplateEngine();
    return templateEngine;
  }


  public static Boolean isTextTemplate(String text) {
    return TextTemplate.TOKEN.matcher(text).find();
  }


  public TextTemplate compile(String string) {

    return new TextTemplate(string);

  }

  @Override
  public void clearCache() {
    // no compile cache
  }


}
