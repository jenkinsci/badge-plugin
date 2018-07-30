package com.jenkinsci.plugins.badge.readme;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.TagNode;
import com.hubspot.jinjava.util.HelperStringTokenizer;

import java.io.File;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

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
    return stream(files).map(File::getName).sorted().map(name -> "- ![alt text](" + path + "/" + name + " \"" + name + "\") " + name).collect(joining("\n"));
  }

  @Override
  public String getEndTagName() {
    return null;
  }

}
