package com.combostrap.template.api;

import java.util.Map;

public interface Template {



  /**
   * Apply a set of key / pair variable
   * The second is an object because
   * a variable may be a collection
   * @param params - the name of the variable and it's value (scalar or collection)
   */
  Template applyVariables(Map<String, Object> params);

  /**
   * @return the result (Resetting also the state)
   */
  String getResult();



}
