package com.jenkinsci.plugins.badge.readme;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.TagNode;
import com.hubspot.jinjava.util.HelperStringTokenizer;
import com.jenkinsci.plugins.badge.annotations.OptionalParam;
import com.jenkinsci.plugins.badge.annotations.Param;
import hudson.Extension;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.sort;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

import javax.swing.text.html.Option;

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

      Optional<MethodParameter[]> constructorParameterNames = getConstructorParameterNames(constructors);

      if (!constructorParameterNames.isPresent()) {
        throw new IllegalStateException("No constructor parameters found");
      }

      List<MethodParameter> requiredParameterNames = new ArrayList<>();
      List<MethodParameter> optionalParameterNames = new ArrayList<>();
      List<MethodParameter> allParams = new ArrayList<>();
      MethodParameter[] constructorParams = constructorParameterNames.get();
      MethodParameter[] methodParams = getOptionalParams(clazz);
      Collections.addAll(allParams, constructorParams);
      Collections.addAll(allParams, methodParams);

      for (MethodParameter methodParameter : allParams) {
        if (methodParameter.isOptional()) {
          optionalParameterNames.add(methodParameter);
        } else {
          requiredParameterNames.add(methodParameter);
        }
      }

      sb.append("// ").append(functionName).append("\n");
      sb.append("// ------------------------------------------\n\n");

      sb.append("/**\n");
      if (optionalParameterNames.size() != 0) {
        sb.append(" * minimal params");
      } else {
        sb.append(" * params");
      }
      sb.append("\n *\n");

      requiredParameterNames.forEach(sb::append);
      sb.append(" */\n");

      sb.append(functionName).append("(")
          .append(requiredParameterNames.stream().map(p -> p.getName() + ": <" + p.getName() + ">")
          .collect(joining(", "))).append(")\n\n");


      if (optionalParameterNames.size() != 0) {
        sb.append("/**\n * all params\n *\n");
        concat(requiredParameterNames.stream(), optionalParameterNames.stream()).forEach(sb::append);

        sb.append(" */\n");

        sb.append(functionName).append("(").append(concat(requiredParameterNames.stream(), optionalParameterNames.stream())
            .map(p -> p.getName() + ": <" + p.getName() + ">").collect(joining(", "))).append(")\n");
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return sb.toString();
  }

  private MethodParameter[] getOptionalParams(Class<?> clazz) {
    return stream(clazz.getMethods()).filter(m -> m.getAnnotation(OptionalParam.class) != null)
        .map(m -> new MethodParameter(m.getAnnotation(OptionalParam.class), m)).sorted().toArray(MethodParameter[]::new);
  }

  /**
   * The constructor parameters need to have @Param in order to get the name of the parameter
   *
   * @param constructors
   * @return
   */
  private Optional<MethodParameter[]> getConstructorParameterNames(Constructor<?>[] constructors) {
    return stream(constructors).filter(c -> c.getAnnotation(DataBoundConstructor.class) != null).map(c -> {

      MethodParameter[] names = new MethodParameter[c.getParameterCount()];

      for (int i = 0; i < c.getParameterCount(); i++) {

        Parameter parameter = c.getParameters()[i];

        Param param = parameter.getAnnotation(Param.class);

        if (param != null) {
          names[i] = new MethodParameter(param);
          continue;
        }

        OptionalParam optionalParam = parameter.getAnnotation(OptionalParam.class);

        if (optionalParam != null) {
          names[i] = new MethodParameter(optionalParam);
          if ("".equals(names[i].name)) {
            throw new IllegalStateException("Optional parameter " + i + " of class " + c.getDeclaringClass().getName() + " needs to have a name.");
          }
        } else {
          throw new IllegalStateException("Constructor argument " + i + " of class " + c.getDeclaringClass().getName() + " needs to have @" + Param.class.getName() + " or @" + OptionalParam.class.getName() + " annotation.");
        }
      }

      sort(names);

      return names;
    }).findFirst();
  }

  private String getFunctionName(Class<?>[] declaredClasses) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    for (Class<?> innerClass : declaredClasses) {
      Extension extension = innerClass.getAnnotation(Extension.class);
      if (extension != null && StepDescriptor.class.isAssignableFrom(innerClass)) {
        return ((StepDescriptor) innerClass.getDeclaredConstructor().newInstance()).getFunctionName();
      }
    }
    return null;
  }

  @Override
  public String getEndTagName() {
    return null;
  }

  private static class MethodParameter implements Comparable<MethodParameter> {
    final String name;
    final String description;
    final boolean optional;

    MethodParameter(Param param) {
      this.name = param.name();
      this.description = param.description();
      this.optional = false;
    }

    MethodParameter(OptionalParam param) {
      this.name = param.name();
      this.description = param.description();
      this.optional = true;
    }

    MethodParameter(OptionalParam param, Method method) {
      String methodName = method.getName().substring(3);
      this.name = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
      this.description = param.description();
      this.optional = true;
    }

    private String getName() {
      return name;
    }

    private boolean isOptional() {
      return optional;
    }

    @Override
    public String toString() {
      return " * " + name + ": " + (optional ? "(optional) " : "") + description + "\n";
    }

    @Override
    public int compareTo(MethodParameter o) {
      return new CompareToBuilder().append(this.optional, o.isOptional()).append(this.name, o.getName()).toComparison();
    }
  }

}
