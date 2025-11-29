package com.combostrap.template;

import com.combostrap.template.api.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextTemplate implements Template {

  protected static final Pattern TOKEN = Pattern.compile("\\$\\{([\\d\\w]+)}|\\$([\\d\\w]+)");

  /**
   * This is a functional interface
   * The function is added to the list dynamically while building {@link #tokens}
   */
  @FunctionalInterface
  protected interface StringBuilding {
    String get(Map<String, Object> params);
  }

  private final StringBuilding[] tokens;
  private final List<String> variablesNameFound = new ArrayList<>();
  private StringBuilder stringResult = new StringBuilder();

  public TextTemplate(String text) {
    final Matcher mat = TOKEN.matcher(text);
    int last = 0;
    final List<StringBuilding> localTokens = new ArrayList<>();
    while (mat.find()) {
      final String constant = text.substring(last, mat.start());
      String matchWithBracket = mat.group(1);
      String matchWithoutBracket = mat.group(2);
      boolean matchWasWithBracket = matchWithBracket != null;
      final String name = matchWasWithBracket ? matchWithBracket : matchWithoutBracket;
      this.variablesNameFound.add(name);
      localTokens.add(variables -> constant);
      localTokens.add(variables -> {
        Object value = variables.get(name);
        if (value != null) {
          return value.toString();
        }
        /*
         * We return the original variable
         * if it has no defined variables
         * (In a pipeline, the target uri allows also template
         * variable that should not be null)
         */
        if (matchWasWithBracket) {
          return "${" + name + "}";
        } else {
          return "$" + name;
        }
      });
      last = mat.end();
    }
    final String tail = text.substring(last);
    if (!tail.isEmpty()) {
      localTokens.add(params -> tail);
    }

    /*
     * Not sure why stringBuildings is an array
     */
    this.tokens = localTokens.toArray(new StringBuilding[0]);
  }

  @Override
  public TextTemplate applyVariables(Map<String, Object> params) {
    for (StringBuilding token : this.tokens) {
      String str = token.get(params);
      if (str == null) {
        str = token.toString();
      }
      stringResult.append(str);
    }
    return this;
  }


  @SuppressWarnings("unused")
  public List<String> getVariableNames() {
    return variablesNameFound;
  }

  /**
   * @return the result and reset the output
   */
  @Override
  public String getResult() {
    String result = this.stringResult.toString();
    this.resetResult();
    return result;
  }

  public void resetResult() {
    this.stringResult = new StringBuilder();
  }
}
