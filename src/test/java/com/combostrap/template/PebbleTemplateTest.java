package com.combostrap.template;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PebbleTemplateTest {


    @Test
    public void StringBase() throws IOException {

        PebbleEngine engine = new PebbleEngine
                .Builder()
                .loader(new StringLoader())
                .build();

        String template = "<p>{{name}}</p>";
        PebbleTemplate compiledTemplate = engine.getTemplate(template);

        Map<String, Object> context = new HashMap<>();
        context.put("name", "Mitchell");

        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);

        String output = writer.toString();
        Assert.assertEquals("<p>Mitchell</p>", output);
    }

    @Test
    public void fileTemplate() throws URISyntaxException, IOException {

        String urlTemplate = Paths.get(PebbleTemplateTest.class.getResource("/templates/").toURI()).toAbsolutePath().toString();
        FileLoader fileLoader = new FileLoader();
        fileLoader.setPrefix(urlTemplate);
        fileLoader.setSuffix(".html");
        PebbleEngine engine = new PebbleEngine
                .Builder()
                .loader(fileLoader)
                .build();

        /**
         * The file load will add the prefix and suffix to `pebble`
         * and create a local path to locate the file
         */
        PebbleTemplate compiledTemplate = engine.getTemplate("pebble");

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

        Map<String, Object> context = new HashMap<>();
        context.put("hello", "Hello World !");
        context.put("records", records);

        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);

        String output = writer.toString();
        String expected = "<!DOCTYPE html>\n" +
                "\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "\n" +
                "<head>\n" +
                "  <title>Pebble</title>\n" +
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
                "    <tr>\n" +
                "    <td>Fresh Sweet Basil</td>\n" +
                "    <td>4.99</td>\n" +
                "    <td>true</td>\n" +
                "  </tr>\n" +
                "    <tr>\n" +
                "    <td>Italian Tomato</td>\n" +
                "    <td>1.25</td>\n" +
                "    <td>false</td>\n" +
                "  </tr>\n" +
                "  </table>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
        Assert.assertEquals(expected, output);
    }
}
