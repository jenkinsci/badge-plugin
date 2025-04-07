/*
 * The MIT License
 *
 * Copyright (c) 2025, Badge Plugin Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.jenkinsci.plugins.badge.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jenkinsci.plugins.badge.action.AbstractBadgeAction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.xml.parsers.DocumentBuilderFactory;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.htmlunit.WebResponse;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

@WithJenkins
class AddBadgeStepTest extends AbstractAddBadgeStepTest {

    @Override
    @Test
    void defaultConstructor() {
        AddBadgeStep step = new AddBadgeStep();
        assertNull(step.getId());
        assertNull(step.getIcon());
        assertNull(step.getText());
        assertNull(step.getCssClass());
        assertNull(step.getStyle());
        assertNull(step.getLink());
        assertNull(step.getTarget());
    }

    @Test
    @Deprecated(since = "2.0", forRemoval = true)
    void color() {
        AddBadgeStep step = (AddBadgeStep) createStep("id", "icon", "text", "cssClass", null, "link", "target");
        assertNull(step.getColor());

        step.setColor("");
        assertEquals("", step.getColor());

        step.setColor("style");
        assertEquals("style", step.getColor());
    }

    @Test
    void addInScriptedPipeline() throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io",
                "_blank");
        runAddJob(step, false, false);
    }

    @Test
    void addInScriptedPipelineInNode() throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io",
                "_blank");
        runAddJob(step, true, false);
    }

    @Test
    void addInDeclarativePipeline() throws Exception {
        AbstractAddBadgeStep step = createStep(UUID.randomUUID().toString(), null, null, null, null, null, null);
        runAddJob(step, false, true);
    }

    @Test
    void modifyInScriptedPipeline() throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io",
                "_blank");
        runModifyJob(step, false, false);
    }

    @Test
    void modifyInScriptedPipelineInNode() throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io",
                "_blank");
        runModifyJob(step, true, false);
    }

    @Test
    void modifyInDeclarativePipeline() throws Exception {
        AbstractAddBadgeStep step = createStep(UUID.randomUUID().toString(), null, "Test Text", null, null, null, null);
        runModifyJob(step, false, true);
    }

    @Test
    void exportedBean() throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io",
                "_blank");
        WorkflowRun job = runAddJob(step, false, false);

        WorkflowRun bean = assertInstanceOf(WorkflowRun.class, job.getApi().bean);
        assertFields(step, bean);

        AbstractBadgeAction action = bean.getAction(AbstractBadgeAction.class);

        try (JenkinsRule.WebClient webClient = r.createWebClient()) {
            // JSON
            WebResponse response = webClient
                    .goTo(job.getUrl() + "api/json", "application/json")
                    .getWebResponse();
            JSONObject json = JSONObject.fromObject(response.getContentAsString())
                    .getJSONArray("actions")
                    .getJSONObject(1);

            Predicate<Object> nullable = value -> !(value instanceof JSONNull);

            assertEquals(action.getClass().getName(), json.get("_class"));
            assertEquals(action.getId(), json.get("id"));
            assertEquals(
                    action.getIcon(),
                    Optional.of(json.get("icon")).filter(nullable).orElse(null));
            assertEquals(
                    action.getText(),
                    Optional.of(json.get("text")).filter(nullable).orElse(null));
            assertEquals(
                    action.getCssClass(),
                    Optional.of(json.get("cssClass")).filter(nullable).orElse(null));
            assertEquals(
                    action.getStyle(),
                    Optional.of(json.get("style")).filter(nullable).orElse(null));
            assertEquals(
                    action.getLink(),
                    Optional.of(json.get("link")).filter(nullable).orElse(null));
            assertEquals(
                    action.getTarget(),
                    Optional.of(json.get("target")).filter(nullable).orElse(null));

            // XML
            response = webClient
                    .goTo(job.getUrl() + "api/xml?xpath=/*/action[2]", "application/xml")
                    .getWebResponse();
            Element xml = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(response.getContentAsStream()))
                    .getDocumentElement();

            Node mockNode = mock(Node.class);
            when(mockNode.getTextContent()).thenReturn(null);

            assertEquals(action.getClass().getName(), xml.getAttribute("_class"));
            assertEquals(action.getId(), xml.getElementsByTagName("id").item(0).getTextContent());
            assertEquals(
                    action.getIcon(),
                    Optional.ofNullable(xml.getElementsByTagName("icon").item(0))
                            .orElse(mockNode)
                            .getTextContent());
            assertEquals(
                    action.getText(),
                    Optional.ofNullable(xml.getElementsByTagName("text").item(0))
                            .orElse(mockNode)
                            .getTextContent());
            assertEquals(
                    action.getCssClass(),
                    Optional.ofNullable(xml.getElementsByTagName("cssClass").item(0))
                            .orElse(mockNode)
                            .getTextContent());
            assertEquals(
                    action.getStyle(),
                    Optional.ofNullable(xml.getElementsByTagName("style").item(0))
                            .orElse(mockNode)
                            .getTextContent());
            assertEquals(
                    action.getLink(),
                    Optional.ofNullable(xml.getElementsByTagName("link").item(0))
                            .orElse(mockNode)
                            .getTextContent());
            assertEquals(
                    action.getTarget(),
                    Optional.ofNullable(xml.getElementsByTagName("target").item(0))
                            .orElse(mockNode)
                            .getTextContent());
        }
    }

    protected WorkflowRun runAddJob(AbstractAddBadgeStep step, boolean inNode, boolean declarativePipeline)
            throws Exception {
        WorkflowJob project = r.createProject(WorkflowJob.class);

        String script = step.toString();

        if (inNode) {
            script = "node() { " + script + " }";
        }

        if (declarativePipeline) {
            script =
                    """
              pipeline {
                  agent any
                  stages {
                      stage('Testing') {
                          steps {
                              %s
                          }
                      }
                  }
              }
              """
                            .formatted(script);
        }

        project.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun run = r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        assertFields(step, run);

        return run;
    }

    protected void runModifyJob(AbstractAddBadgeStep step, boolean inNode, boolean declarativePipeline)
            throws Exception {
        WorkflowJob project = r.createProject(WorkflowJob.class);

        String actualText = step.getText();
        step.setText(UUID.randomUUID().toString());
        assertNotEquals(actualText, step.getText());

        String script = """
        def badge = %s
        badge.setText('%s')
        """.formatted(step, actualText);

        if (inNode) {
            script = "node() { " + script + " }";
        }

        if (declarativePipeline) {
            script =
                    """
              pipeline {
                  agent any
                  stages {
                      stage('Testing') {
                          steps {
                              script {
                                  %s
                              }
                          }
                      }
                  }
              }
              """
                            .formatted(script);
        }

        project.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun run = r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        step.setText(actualText);
        assertEquals(actualText, step.getText());

        assertFields(step, run);
    }

    protected void assertFields(AbstractAddBadgeStep step, WorkflowRun run) {
        List<AbstractBadgeAction> badgeActions = run.getActions(AbstractBadgeAction.class);
        assertEquals(1, badgeActions.size());

        AbstractBadgeAction action = badgeActions.get(0);
        assertEquals(step.getId(), action.getId());
        assertEquals(step.getIcon(), action.getIcon());
        assertEquals(step.getText(), action.getText());
        assertEquals(step.getCssClass(), action.getCssClass());
        assertEquals(step.getStyle(), action.getStyle());
        assertEquals(step.getLink(), action.getLink());
        assertEquals(step.getTarget(), action.getTarget());
    }

    @Override
    protected AbstractAddBadgeStep createStep(
            String id, String icon, String text, String cssClass, String style, String link, String target) {
        return new AddBadgeStep(id, icon, text, cssClass, style, link, target);
    }
}
