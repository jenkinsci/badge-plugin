package com.jenkinsci.plugins.badge.readme;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.TagNode;
import com.hubspot.jinjava.util.HelperStringTokenizer;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ListImagesTag implements Tag {


  @Override
  public String getName() {
    return "list_images";
  }

  @Override
  public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
    HelperStringTokenizer helper = new HelperStringTokenizer(tagNode.getHelpers());


    if (!helper.hasNext()) {
      return null;
    }

    String path = helper.next();


    File[] files = new File(path).listFiles();
    if (files == null) {
      return null;
    }
    return Arrays.stream(files).map(f -> "![alt text](" + path + "/" + f.getName() + " \"" + f.getName() + "\")").collect(Collectors.joining("\n"));
  }

  @Override
  public String getEndTagName() {
    return null;
  }

}
