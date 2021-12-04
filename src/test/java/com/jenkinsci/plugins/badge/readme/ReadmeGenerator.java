package com.jenkinsci.plugins.badge.readme;

import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ReadmeGenerator {
  public static void main(String... args) throws IOException {
    Jinjava jinjava = new Jinjava();
    jinjava.getGlobalContext().registerTag(new ListImagesTag());
    jinjava.getGlobalContext().registerTag(new DescribeStepTag());

    Map<String, Object> context = new HashMap<>();

    String template = Resources.toString(Resources.getResource("readme/README.tmpl"), StandardCharsets.UTF_8);

    String renderedTemplate = jinjava.render(template, context);

    try (PrintWriter out = new PrintWriter("README.md")) {
      out.println(renderedTemplate);
    }

  }
}
