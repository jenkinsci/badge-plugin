package com.jenkinsci.plugins.badge.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class UITest {

    @Nested
    class Badge {

        @Test
        void iconWithLink(JenkinsRule r) throws Throwable {
            AddBadgeStep step = new AddBadgeStep(
                    null,
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io");
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement builds = overview.getElementById("jenkins-builds");

                assertEquals(6, builds.getElementsByTagName("span").size());

                DomElement badge = builds.getElementsByTagName("a").get(3);
                DomElement icon = badge.getLastElementChild();

                assertEquals("a", badge.getTagName());
                assertEquals("svg", icon.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals(step.getCssClass(), badge.getAttribute("class"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
                assertEquals(step.getLink(), badge.getAttribute("href"));
            }
        }

        @Test
        void iconWithoutLink(JenkinsRule r) throws Throwable {
            AddBadgeStep step = new AddBadgeStep(
                    null, "symbol-rocket plugin-ionicons-api", "Test Text", "Test Class", "Test Style", null);
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement builds = overview.getElementById("jenkins-builds");

                assertEquals(6, builds.getElementsByTagName("span").size());

                DomElement badge = builds.getElementsByTagName("span").get(2);
                DomElement icon = badge.getLastElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals(step.getCssClass(), badge.getAttribute("class"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void textWithLink(JenkinsRule r) throws Throwable {
            AddBadgeStep step =
                    new AddBadgeStep(null, null, "Test Text", "Test Class", "Test Style", "https://jenkins.io");
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement builds = overview.getElementById("jenkins-builds");

                assertEquals(5, builds.getElementsByTagName("span").size());

                DomElement badge = builds.getElementsByTagName("a").get(3);

                assertEquals("a", badge.getTagName());

                assertEquals(step.getText(), badge.getTextContent());
                assertEquals(step.getCssClass(), badge.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
                assertEquals(step.getLink(), badge.getAttribute("href"));
            }
        }

        @Test
        void textWithoutLink(JenkinsRule r) throws Throwable {
            AddBadgeStep step = new AddBadgeStep(null, null, "Test Text", "Test Class", "Test Style", null);
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement builds = overview.getElementById("jenkins-builds");

                assertEquals(5, builds.getElementsByTagName("span").size());

                DomElement badge = builds.getElementsByTagName("span").get(2);

                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), badge.getTextContent());
                assertEquals(step.getCssClass(), badge.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void info(JenkinsRule r) throws Throwable {
            AddInfoBadgeStep step = new AddInfoBadgeStep(null, "Test Text", null);
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement builds = overview.getElementById("jenkins-builds");

                assertEquals(6, builds.getElementsByTagName("span").size());

                DomElement badge = builds.getElementsByTagName("span").get(2);
                DomElement icon = badge.getLastElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void warning(JenkinsRule r) throws Throwable {
            AddWarningBadgeStep step = new AddWarningBadgeStep(null, "Test Text", null);
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement builds = overview.getElementById("jenkins-builds");

                assertEquals(6, builds.getElementsByTagName("span").size());

                DomElement badge = builds.getElementsByTagName("span").get(2);
                DomElement icon = badge.getLastElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void error(JenkinsRule r) throws Throwable {
            AddErrorBadgeStep step = new AddErrorBadgeStep(null, "Test Text", null);
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement builds = overview.getElementById("jenkins-builds");

                assertEquals(6, builds.getElementsByTagName("span").size());

                DomElement badge = builds.getElementsByTagName("span").get(2);
                DomElement icon = badge.getLastElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void remove(JenkinsRule r) throws Throwable {
            AddBadgeStep addStep = new AddBadgeStep(
                    UUID.randomUUID().toString(),
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io");
            RemoveBadgesStep removeStep = new RemoveBadgesStep(addStep.getId());
            WorkflowJob job = runJob(r, addStep, removeStep);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement builds = overview.getElementById("jenkins-builds");

                assertEquals(4, builds.getElementsByTagName("span").size());
            }
        }
    }

    @Nested
    class Summary {

        @Test
        void iconWithTextWithLink(JenkinsRule r) throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null,
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io");
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertEquals(2, overview.getElementsByTagName("tr").size());

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement link = summary.getLastElementChild().getFirstElementChild();
                DomElement text = link.getFirstElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("a", link.getTagName());
                assertEquals("span", text.getTagName());

                assertEquals(step.getText(), text.getTextContent());
                assertEquals(step.getCssClass(), text.getAttribute("class"));
                assertEquals(step.getStyle(), text.getAttribute("style"));
                assertEquals(step.getLink(), link.getAttribute("href"));
            }
        }

        @Test
        void iconWithTextWithoutLink(JenkinsRule r) throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null, "symbol-rocket plugin-ionicons-api", "Test Text", "Test Class", "Test Style", null);
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertEquals(2, overview.getElementsByTagName("tr").size());

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement text = summary.getLastElementChild().getFirstElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", text.getTagName());

                assertEquals(step.getText(), text.getTextContent());
                assertEquals(step.getCssClass(), text.getAttribute("class"));
                assertEquals(step.getStyle(), text.getAttribute("style"));
            }
        }

        @Test
        void iconWithoutTextWithoutLink(JenkinsRule r) throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null, "symbol-rocket plugin-ionicons-api", null, "Test Class", "Test Style", null);
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertEquals(2, overview.getElementsByTagName("tr").size());

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement text = summary.getLastElementChild().getFirstElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", text.getTagName());

                assertTrue(StringUtils.isBlank(text.getTextContent()));
                assertEquals(step.getCssClass(), text.getAttribute("class"));
                assertEquals(step.getStyle(), text.getAttribute("style"));
            }
        }

        @Test
        void textWithoutIconWithLink(JenkinsRule r) throws Throwable {
            AddSummaryStep step =
                    new AddSummaryStep(null, null, "Test Text", "Test Class", "Test Style", "https://jenkins.io");
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertEquals(2, overview.getElementsByTagName("tr").size());

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement link = summary.getLastElementChild().getFirstElementChild();
                DomElement text = link.getFirstElementChild();

                assertEquals("img", icon.getTagName());
                assertEquals("a", link.getTagName());
                assertEquals("span", text.getTagName());

                assertEquals("/jenkins" + Jenkins.RESOURCE_PATH + "/images/16x16/empty.png", icon.getAttribute("src"));
                assertEquals(step.getText(), text.getTextContent());
                assertEquals(step.getCssClass(), text.getAttribute("class"));
                assertEquals(step.getStyle(), text.getAttribute("style"));
                assertEquals(step.getLink(), link.getAttribute("href"));
            }
        }

        @Test
        void textWithoutIconWithoutLink(JenkinsRule r) throws Throwable {
            AddSummaryStep step = new AddSummaryStep(null, null, "Test Text", "Test Class", "Test Style", null);
            WorkflowJob job = runJob(r, step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertEquals(2, overview.getElementsByTagName("tr").size());

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement text = summary.getLastElementChild().getFirstElementChild();

                assertEquals("img", icon.getTagName());
                assertEquals("span", text.getTagName());

                assertEquals("/jenkins" + Jenkins.RESOURCE_PATH + "/images/16x16/empty.png", icon.getAttribute("src"));
                assertEquals(step.getText(), text.getTextContent());
                assertEquals(step.getCssClass(), text.getAttribute("class"));
                assertEquals(step.getStyle(), text.getAttribute("style"));
            }
        }

        @Test
        void remove(JenkinsRule r) throws Throwable {
            AddSummaryStep addStep = new AddSummaryStep(
                    UUID.randomUUID().toString(),
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io");
            RemoveSummariesStep removeStep = new RemoveSummariesStep(addStep.getId());
            WorkflowJob job = runJob(r, addStep, removeStep);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertEquals(1, overview.getElementsByTagName("tr").size());
            }
        }
    }

    private static WorkflowJob runJob(JenkinsRule r, AbstractAddBadgeStep addStep, AbstractRemoveBadgesStep removeStep)
            throws Exception {
        WorkflowJob project = r.jenkins.createProject(WorkflowJob.class, "project");

        String script =
                """
                        pipeline {
                            agent any
                            stages {
                                stage('Testing') {
                                    steps {
                                        %s
                                        %s
                                    }
                                }
                            }
                        }
                        """
                        .formatted(addStep.toString(), removeStep != null ? removeStep.toString() : "");

        project.setDefinition(new CpsFlowDefinition(script, true));
        r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        return project;
    }
}
