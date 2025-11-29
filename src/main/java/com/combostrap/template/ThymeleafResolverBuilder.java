package com.combostrap.template;

import net.bytle.exception.InternalException;
import net.bytle.java.JavaEnvs;
import net.bytle.java.Javas;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.Collections;

/**
 * A wrapper / helper around the construction of a {@link AbstractConfigurableTemplateResolver Resolver}
 * The resolver goal is to search the templates
 * <p>
 * It searches:
 * * in dev on the file system without any cache to allow quick dev
 * * in prod, with cache from the main resources jar location
 */
public class ThymeleafResolverBuilder {


  private Boolean cache;
  private String resourcePath;
  private Class<?> inModuleFromClass = null;
  private TemplateMode templateMode;
  private int order = 0;

  public static ThymeleafResolverBuilder create() {
    return new ThymeleafResolverBuilder();
  }

  public ThymeleafResolverBuilder setCache(Boolean cache) {
    this.cache = cache;
    return this;
  }

  public ThymeleafResolverBuilder setResourcePath(String templatesResourceRoot) {
    this.resourcePath = templatesResourceRoot;
    return this;
  }


  public AbstractConfigurableTemplateResolver build() {

    AbstractConfigurableTemplateResolver templateResolver;

    if (JavaEnvs.isDev()) {

      /**
       * in dev,
       * we get the file directly from the {@link FileTemplateResolver file system}
       * without cache
       * to be able to modify on the fly
       */
      String pathToResourcesDirectory = "src/main/resources";

      /**
       * If the template comes from another module
       * than the actual one
       */
      if (this.inModuleFromClass != null) {
        Path modulePath;
        try {
          modulePath = Javas.getModulePath(this.inModuleFromClass);
        } catch (NotDirectoryException e) {
          throw new InternalException("The module path should exist in dev", e);
        }

        pathToResourcesDirectory = modulePath
          .resolve(pathToResourcesDirectory)
          .toAbsolutePath()
          .toString();
      }

      templateResolver = new FileTemplateResolver();
      templateResolver.setPrefix(pathToResourcesDirectory + this.resourcePath);
      templateResolver.setCacheable(false);

    } else {

      /**
       * Production
       * We get the files from the resources jar directory
       */
      templateResolver = new ClassLoaderTemplateResolver();
      templateResolver.setPrefix(this.resourcePath);
      templateResolver.setCacheable(this.cache);
      templateResolver.setCacheTTLMs(null); // LRU cache algo, entries would be cached until expelled

    }
    templateResolver.setCharacterEncoding("utf-8");
    templateResolver.setOrder(this.order);

    switch (this.templateMode){
      case HTML:
        templateResolver.setResolvablePatterns(Collections.singleton("*.html"));
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        break;
      case TEXT:
        templateResolver.setResolvablePatterns(Collections.singleton("*.txt"));
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        break;
      case JAVASCRIPT:
        templateResolver.setResolvablePatterns(Collections.singleton("*.js"));
        templateResolver.setSuffix(".js");
        templateResolver.setTemplateMode(TemplateMode.JAVASCRIPT);
        break;
      default:
        throw new RuntimeException("The template mode ("+this.templateMode+") is unknown");
    }
    return templateResolver;

  }

  /**
   * If the template comes from another modules,
   * the file system path resolver, we get it from the module
   * of this class
   */
  public ThymeleafResolverBuilder setInModuleFromClass(Class<?> fromClass) {
    this.inModuleFromClass = fromClass;
    return this;
  }

  /**
   * @param templateMode - the template mode (how the memory object is created)
   */
  public ThymeleafResolverBuilder setTemplateMode(TemplateMode templateMode) {
    this.templateMode = templateMode;
    return this;
  }

  /**
   * @param order - the priority order in case of conflict
   */
  public ThymeleafResolverBuilder setOrder(int order) {
    this.order =order;
    return this;
  }
}
