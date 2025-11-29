package com.combostrap.template.api;

public interface TemplateEngine {


  /**
   *
   * @param string the template string
   * @return a compiled template (same idea than regexp)
   */
  Template compile(String string);

  /**
   * Delete the compile cache
   * Used when a new service starts up for instance with the html template in the resource map.
   */
  @SuppressWarnings("unused")
  void clearCache();

}
