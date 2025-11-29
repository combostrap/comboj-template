package com.combostrap.template;

import org.junit.Assert;
import org.junit.Test;

public class TextTemplateEngineTest {




  @Test
  public void isTemplate() {

    Assert.assertTrue("This is a template", TextTemplateEngine.isTextTemplate("Dear ${name}, we are \n"));
    Assert.assertTrue("This is a template", TextTemplateEngine.isTextTemplate("Dear $name, we are \n"));
    Assert.assertFalse(TextTemplateEngine.isTextTemplate("no dollar sign"));

  }
}
