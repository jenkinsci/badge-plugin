package com.jenkinsci.plugins.badge.readme;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.TagNode;
import com.hubspot.jinjava.util.HelperStringTokenizer;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Arrays.sort;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

public class DescribeStepTag implements Tag {


  @Override
  public String getName() {
    return "describe_step";
  }

  @Override
  public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
    HelperStringTokenizer helper = new HelperStringTokenizer(tagNode.getHelpers());


    if (!helper.hasNext()) {
      return null;
    }

    StringBuilder sb = new StringBuilder();

    String className = helper.next();

    try {
      Class<?> clazz = Class.forName(className);
      Constructor<?>[] constructors = clazz.getConstructors();

      Class<?>[] declaredClasses = clazz.getDeclaredClasses();


      String functionName = getFunctionName(declaredClasses);

      Optional<String[]> constructorParameterNames = getConstructorParameterNames(constructors);

      if (!constructorParameterNames.isPresent()) {
        throw new IllegalStateException("No constructor parameters found");
      }

      String[] constructorParams = constructorParameterNames.get();

      String[] optionalParameterNames = getOptionalParams(clazz);

      sb.append("// ").append(functionName).append("\n");
      sb.append("// ------------------------------------------\n\n");

      if (optionalParameterNames.length != 0) {
        sb.append("// minimal params\n");
      } else {
        sb.append("// params\n");
      }

      sb.append(functionName).append("(").append(stream(constructorParams).collect(joining(", "))).append(")\n\n");

      if (optionalParameterNames.length != 0) {
        sb.append("// all params\n");
        sb.append(functionName).append("(").append(concat(stream(constructorParams), stream(optionalParameterNames)).collect(joining(", "))).append(")\n");
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    return sb.toString();
  }

  private String[] getOptionalParams(Class<?> clazz) {
    return stream(clazz.getMethods()).filter(m -> m.getAnnotation(DataBoundSetter.class) != null).map(m -> {
      String name = m.getName().substring(3);
      return name.substring(0, 1).toLowerCase() + name.substring(1);
    }).sorted().toArray(String[]::new);
  }

  /**
   * The constructor parameters need to have @Named in order to get the name of the parameter
   * @param constructors
   * @return
   */
  private Optional<String[]> getConstructorParameterNames(Constructor<?>[] constructors) {
    return stream(constructors).filter(c -> c.getAnnotation(DataBoundConstructor.class) != null).map(c -> {

      String[] names = new String[c.getParameterCount()];

      for (int i = 0; i < c.getParameterCount(); i++) {

        Parameter parameter = c.getParameters()[i];

        Named named = parameter.getAnnotation(Named.class);

        if (named == null) {
          throw new IllegalStateException("Constructor argument " + i + " of class " + c.getDeclaringClass().getName() + " needs to have @" + Named.class.getName() + " annotation.");
        }

        names[i] = named.value();

      }

      sort(names);

      return names;
    }).findFirst();
  }

  private String getFunctionName(Class<?>[] declaredClasses) throws InstantiationException, IllegalAccessException {
    for (Class<?> innerClass : declaredClasses) {
      Extension extension = innerClass.getAnnotation(Extension.class);
      if (extension != null && StepDescriptor.class.isAssignableFrom(innerClass)) {
        return ((StepDescriptor) innerClass.newInstance()).getFunctionName();
      }
    }
    return null;
  }

  @Override
  public String getEndTagName() {
    return null;
  }

}
