package com.combostrap.template;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;


public class ThymeleafTemplateTest {

  public static String EXPECTED = "<!DOCTYPE html>\n" +
    "\n" +
    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
    "\n" +
    "<head>\n" +
    "  <title>Good Thymes Virtual Grocery</title>\n" +
    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
    "</head>\n" +
    "\n" +
    "<body>\n" +
    "\n" +
    "<p>Hello World !</p>\n" +
    "\n" +
    "<h1>Product list</h1>\n" +
    "\n" +
    "<table>\n" +
    "  <tr>\n" +
    "    <th>NAME</th>\n" +
    "    <th>PRICE</th>\n" +
    "    <th>IN STOCK</th>\n" +
    "  </tr>\n" +
    "  <tr>\n" +
    "    <td>Fresh Sweet Basil</td>\n" +
    "    <td>4.99</td>\n" +
    "    <td>yes</td>\n" +
    "  </tr>\n" +
    "  <tr>\n" +
    "    <td>Italian Tomato</td>\n" +
    "    <td>1.25</td>\n" +
    "    <td>no</td>\n" +
    "  </tr>\n" +
    "</table>\n" +
    "\n" +
    "</body>\n" +
    "\n" +
    "</html>\n";

  /**
   * <a href="https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#template-resolvers">...</a>
   */
  @Test
  public void textTemplateExample() {

    /*
     * Templates are resolved by their name (or content) and also (optionally) their
     * owner template in case we are trying to resolve a fragment for another template.
     * Will return null if template cannot be handled by this template resolver.
     * These objects are in charge of determining how the templates are found
     */
    StringTemplateResolver templateResolver = new StringTemplateResolver();

    // HTML is the default mode (ie it expect XML as input with th attributes),
    // but we set it anyway for better understanding of code
    Assert.assertEquals(TemplateMode.HTML, templateResolver.getTemplateMode());
    templateResolver.setTemplateMode(TemplateMode.HTML);


    // Cache of template is set to false by default. Set to false if you want templates to
    // be automatically updated when modified.
    Assert.assertFalse(templateResolver.isCacheable());
    templateResolver.setCacheable(true);

    // By default, the cache lived in a LRU fashion
    // entries would be cached until expelled by the LRU algorithm
    Assert.assertNull(templateResolver.getCacheTTLMs());
    // Template cache TTL=1h. If not set, entries would be cached until expelled
    templateResolver.setCacheTTLMs(3600000L);


    /**
     * Template Engine objects are implementations of the org.thymeleaf.ITemplateEngine interface.
     */
    TemplateEngine templateEngine = new TemplateEngine();
    Set<ITemplateResolver> templateResolvers = templateEngine.getTemplateResolvers();
    Assert.assertEquals(0, templateResolvers.size());
    //templateEngine.setTemplateResolver(templateResolver);

    /**
     * A Thymeleaf context is an object implementing the org.thymeleaf.context.IContext interface.
     * Contexts should contain all the data required for an execution of the template engine in a variables map,
     * and also reference the locale that must be used for externalized messages.
     */
    Map<String, Object> var = new HashMap<>();
    var.put("message", "Hello World !");
    IContext context = new Context(Locale.US, var);

    String template = "<h1 th:text=\"${message}\">A message will go here!</h1>";

    String result = templateEngine.process(template, context);

    String expected = "<h1>Hello World !</h1>";
    Assert.assertEquals(expected, result);

  }

  @Test
  public void urlTemplateExample() throws URISyntaxException, MalformedURLException {

    /*
     * Templates are resolved by their name (or content) and also (optionally) their
     * owner template in case we are trying to resolve a fragment for another template.
     * Will return null if template cannot be handled by this template resolver.
     * These objects are in charge of determining how our templates will be accessed
     */
    UrlTemplateResolver templateResolver = new UrlTemplateResolver();

    // HTML is the default mode, but we set it anyway for better understanding of code
    templateResolver.setTemplateMode(TemplateMode.HTML);

    // Template cache TTL=1h. If not set, entries would be cached until expelled
    templateResolver.setCacheTTLMs(3600000L);

    // Cache of template is set to true by default. Set to false if you want templates to
    // be automatically updated when modified.
    templateResolver.setCacheable(false);


    /**
     * Template Engine objects are implementations of the org.thymeleaf.ITemplateEngine interface.
     */
    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    /**
     * A Thymeleaf context is an object implementing the org.thymeleaf.context.IContext interface.
     * Contexts should contain all the data required for an execution of the template engine in a variables map,
     * and also reference the locale that must be used for externalized messages.
     */
    Map<String, Object> var = new HashMap<>();

    /**
     * Single value
     */
    var.put("welcome", "Hello World !");
    /**
     * Record are in map
     */
    List<Map<String, Object>> records = new ArrayList<>();
    HashMap<String, Object> record = new HashMap<>();
    record.put("NAME", "Fresh Sweet Basil");
    record.put("PRICE", 4.99);
    record.put("IN_STOCK", true);
    records.add(record);
    record = new HashMap<>();
    record.put("NAME", "Italian Tomato");
    record.put("PRICE", 1.25);
    record.put("IN_STOCK", false);
    records.add(record);
    var.put("records", records);
    IContext context = new Context(Locale.US, var);

    String urlTemplate = Objects.requireNonNull(ThymeleafTemplateTest.class.getResource("/templates/thymeleaf.html")).toURI().toURL().toString();
    String result = templateEngine.process(urlTemplate, context);

    Assert.assertEquals(EXPECTED, result);

  }

}
