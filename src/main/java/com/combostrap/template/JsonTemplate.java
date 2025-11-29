package com.combostrap.template;

import com.google.gson.*;
import net.bytle.log.Log;
import net.bytle.log.Logs;
import com.combostrap.template.api.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * A JSON template engine.
 * <p>
 * You pass a true json where the key or value can contain variable ${variable}
 * <p>
 *
 * Inspiration for the function interface comes from
 * from <a href="https://codereview.stackexchange.com/questions/102339/fastest-possible-text-template-for-repeated-use">...</a>
 */
public class JsonTemplate implements Template {

  public static final Log LOGGER = Logs.createFromClazz(JsonTemplate.class);

  private final JsonBuilding[] buildSequence;
  private JsonObject jsonObject = new JsonObject();

  public JsonTemplate(JsonBuilding[] toArray) {

    this.buildSequence = toArray;

  }

  /**
   * Tokenize a string in a sequence of string
   * <p>
   * TODO: This is the same than {@link TextTemplateEngine}
   *    Not sure why text template is not used
   *
   * @param text - the text to tokenize
   * @return the tokens
   */
  private static List<TextTemplate.StringBuilding> getStringBuildings(String text) {
    final Matcher mat = TextTemplate.TOKEN.matcher(text);
    int last = 0;
    final List<TextTemplate.StringBuilding> sequenceText = new ArrayList<>();
    while (mat.find()) {
      final String constant = text.substring(last, mat.start());
      String matchWithBracket = mat.group(1);
      String matchWithoutBracket = mat.group(2);
      final String name = matchWithBracket != null ? matchWithBracket : matchWithoutBracket;
      sequenceText.add(params -> constant);
      sequenceText.add(params -> params.get(name).toString());
      last = mat.end();
    }
    final String tail = text.substring(last);
    if (!tail.isEmpty()) {
      sequenceText.add(params -> tail);
    }
    return sequenceText;
  }

  @Override
  public String getResult() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String result = gson.toJson(jsonObject);
    this.jsonObject = new JsonObject();
    return result;
  }


  @FunctionalInterface
  private interface JsonBuilding {
    void apply(com.google.gson.JsonObject jsonObject, Map<String, Object> params);
  }


  public static JsonTemplate compile(String string) {
    /**
     * The sequence of Json building
     */
    final List<JsonBuilding> sequenceJson = new ArrayList<>();

    com.google.gson.JsonObject jsonTemplate = JsonParser.parseString(string).getAsJsonObject();

    Map<String, List<TextTemplate.StringBuilding>> keyMap = new HashMap<>();
    Map<String, List<TextTemplate.StringBuilding>> valueMap = new HashMap<>();
    buildMap(null, jsonTemplate, keyMap, valueMap);

    // Add key building
    sequenceJson.add((jsonObject, params) -> buildJson(jsonTemplate,jsonObject,keyMap,valueMap,params));


    return new JsonTemplate(sequenceJson.toArray(new JsonBuilding[0]));
  }

  private static void buildJson(JsonObject template, JsonObject jsonObject, Map<String, List<TextTemplate.StringBuilding>> keyMap, Map<String, List<TextTemplate.StringBuilding>>  valueMap, Map<String, Object> params) {
    for (Map.Entry<String, JsonElement> jsonEntry : template.entrySet()) {
      String key = jsonEntry.getKey();
      StringBuilder keyFunc = new StringBuilder();
      for (TextTemplate.StringBuilding lu : keyMap.get(key)) {
        keyFunc.append(lu.get(params));
      }
      JsonElement value = jsonEntry.getValue();
      if (value instanceof JsonObject) {
        JsonObject childJsonObject = (JsonObject) jsonObject.get(keyFunc.toString());
        if (childJsonObject==null) {
          childJsonObject = new JsonObject();
          jsonObject.add(keyFunc.toString(),childJsonObject);
        }
        buildJson((JsonObject)value,childJsonObject, keyMap, valueMap, params);
      } else {
        StringBuilder valueFunc = new StringBuilder();
        for (TextTemplate.StringBuilding lu : valueMap.get(key)) {
          valueFunc.append(lu.get(params));
        }
        jsonObject.addProperty(keyFunc.toString(), valueFunc.toString());
      }
    }
  }

  /**
   * Build the map of sequence of string building for the key and value
   * @param level the hierarchy level ?
   * @param jsonObject the json Object
   * @param keyMap ??
   * @param valueMap ??
   */
  private static void buildMap(String level, JsonObject jsonObject, Map<String, List<TextTemplate.StringBuilding>> keyMap, Map<String, List<TextTemplate.StringBuilding>> valueMap) {
    for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
      /**
       * Key
       */
      String key = jsonEntry.getKey();
      final List<TextTemplate.StringBuilding> keySequenceText = getStringBuildings(key);
      keyMap.put(key, keySequenceText);

      /**
       * Value
       */
      JsonElement value = jsonEntry.getValue();
      if (value instanceof JsonObject) {
        String childLevel;
        if (level != null) {
          childLevel = level + "." + key;
        } else {
          childLevel = key;
        }
        buildMap(childLevel, (JsonObject) value, keyMap, valueMap);
      } else if (value instanceof JsonPrimitive){
        final List<TextTemplate.StringBuilding> valueSequenceText = getStringBuildings(value.getAsString());
        valueMap.put(key, valueSequenceText);
      } else {
        LOGGER.warning("Json Template: The class ("+value.getClass().getSimpleName()+") of the value ("+value+") is not implemented and could be compiled");
      }
    }
  }


  @Override
  public JsonTemplate applyVariables(Map<String, Object> params) {

    for (JsonBuilding lu : buildSequence) {
      lu.apply(jsonObject, params);
    }
    return this;
  }

}
