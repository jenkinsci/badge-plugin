package com.jenkinsci.plugins.badge.dsl;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.StringUtils;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class UITest {

    private static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Nested
    class Badge {

        @Test
        void iconWithLink() throws Throwable {
            AddBadgeStep step = new AddBadgeStep(
                    null,
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io",
                    "_blank");
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#jenkins-builds .app-builds-container__item__inner__controls a"),
                                Objects::nonNull);
                DomElement icon = badge.getLastElementChild();

                assertEquals("a", badge.getTagName());
                assertEquals("svg", icon.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals(step.getCssClass(), badge.getAttribute("class"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
                assertEquals(step.getLink(), badge.getAttribute("href"));
                assertEquals(step.getTarget(), badge.getAttribute("target"));
            }
        }

        @Test
        void iconWithoutLink() throws Throwable {
            AddBadgeStep step = new AddBadgeStep(
                    null, "symbol-rocket plugin-ionicons-api", "Test Text", "Test Class", "Test Style", null, null);
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#jenkins-builds .app-builds-container__item__inner__controls span"),
                                Objects::nonNull);
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
        void textWithLink() throws Throwable {
            AddBadgeStep step = new AddBadgeStep(
                    null, null, "Test Text", "Test Class", "Test Style", "https://jenkins.io", "_blank");
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#jenkins-builds .app-builds-container__item__inner__controls a"),
                                Objects::nonNull);

                assertEquals("a", badge.getTagName());

                assertEquals(step.getText(), badge.getTextContent());
                assertEquals(step.getCssClass(), badge.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
                assertEquals(step.getLink(), badge.getAttribute("href"));
                assertEquals(step.getTarget(), badge.getAttribute("target"));
            }
        }

        @Test
        void textWithoutLink() throws Throwable {
            AddBadgeStep step = new AddBadgeStep(null, null, "Test Text", "Test Class", "Test Style", null, null);
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#jenkins-builds .app-builds-container__item__inner__controls span"),
                                Objects::nonNull);

                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), badge.getTextContent());
                assertEquals(step.getCssClass(), badge.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void info() throws Throwable {
            AddInfoBadgeStep step = new AddInfoBadgeStep(null, "Test Text", null, null);
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#jenkins-builds .app-builds-container__item__inner__controls span"),
                                Objects::nonNull);
                DomElement icon = badge.getLastElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void warning() throws Throwable {
            AddWarningBadgeStep step = new AddWarningBadgeStep(null, "Test Text", null, null);
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#jenkins-builds .app-builds-container__item__inner__controls span"),
                                Objects::nonNull);
                DomElement icon = badge.getLastElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void error() throws Throwable {
            AddErrorBadgeStep step = new AddErrorBadgeStep(null, "Test Text", null, null);
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#jenkins-builds .app-builds-container__item__inner__controls span"),
                                Objects::nonNull);
                DomElement icon = badge.getLastElementChild();

                assertEquals("svg", icon.getTagName());
                assertEquals("span", badge.getTagName());

                assertEquals(step.getText(), icon.getAttribute("data-html-tooltip"));
                assertEquals("icon-sm", icon.getAttribute("class"));
                assertEquals(step.getStyle(), badge.getAttribute("style"));
            }
        }

        @Test
        void remove() throws Throwable {
            AddBadgeStep addStep = new AddBadgeStep(
                    UUID.randomUUID().toString(),
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io",
                    "_blank");
            RemoveBadgesStep removeStep = new RemoveBadgesStep(addStep.getId());
            WorkflowJob job = runJob(addStep, removeStep);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#jenkins-builds .app-builds-container__item__inner__controls"),
                                Objects::nonNull);
                assertEquals(0, badge.getElementsByTagName("span").size());
            }
        }
    }

    @Nested
    class Summary {

        @Test
        void iconWithTextWithLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null,
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io",
                    "_blank");
            WorkflowJob job = runJob(step, null);

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
                assertEquals(step.getCssClass(), link.getAttribute("class"));
                assertEquals(step.getStyle(), link.getAttribute("style"));
                assertEquals(step.getLink(), link.getAttribute("href"));
                assertEquals(step.getTarget(), link.getAttribute("target"));
            }
        }

        @Test
        void iconWithTextWithoutLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null, "symbol-rocket plugin-ionicons-api", "Test Text", "Test Class", "Test Style", null, null);
            WorkflowJob job = runJob(step, null);

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
        void iconWithoutTextWithoutLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null, "symbol-rocket plugin-ionicons-api", null, "Test Class", "Test Style", null, null);
            WorkflowJob job = runJob(step, null);

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
        void textWithoutIconWithLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null, null, "Test Text", "Test Class", "Test Style", "https://jenkins.io", "_blank");
            WorkflowJob job = runJob(step, null);

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
                assertEquals(step.getCssClass(), link.getAttribute("class"));
                assertEquals(step.getStyle(), link.getAttribute("style"));
                assertEquals(step.getLink(), link.getAttribute("href"));
                assertEquals(step.getTarget(), link.getAttribute("target"));
            }
        }

        @Test
        void textWithoutIconWithoutLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(null, null, "Test Text", "Test Class", "Test Style", null, null);
            WorkflowJob job = runJob(step, null);

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
        void remove() throws Throwable {
            AddSummaryStep addStep = new AddSummaryStep(
                    UUID.randomUUID().toString(),
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io",
                    "_blank");
            RemoveSummariesStep removeStep = new RemoveSummariesStep(addStep.getId());
            WorkflowJob job = runJob(addStep, removeStep);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertEquals(1, overview.getElementsByTagName("tr").size());
            }
        }
    }

    private static WorkflowJob runJob(AbstractAddBadgeStep addStep, AbstractRemoveBadgesStep removeStep)
            throws Exception {
        WorkflowJob project = r.createProject(WorkflowJob.class);

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
