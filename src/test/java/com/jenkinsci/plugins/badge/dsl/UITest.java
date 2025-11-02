package com.jenkinsci.plugins.badge.dsl;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import jenkins.model.Jenkins;
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

                assertThat(badge.getTagName(), is("a"));
                assertThat(icon.getTagName(), is("svg"));

                assertThat(icon.getAttribute("data-html-tooltip"), is(step.getText()));
                assertThat(badge.getAttribute("class"), is(step.getCssClass()));
                assertThat(icon.getAttribute("class"), is("icon-sm"));
                assertThat(badge.getAttribute("style"), is(step.getStyle()));
                assertThat(badge.getAttribute("href"), is(step.getLink()));
                assertThat(badge.getAttribute("target"), is(step.getTarget()));
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

                assertThat(icon.getTagName(), is("svg"));
                assertThat(badge.getTagName(), is("span"));

                assertThat(icon.getAttribute("data-html-tooltip"), is(step.getText()));
                assertThat(badge.getAttribute("class"), is(step.getCssClass()));
                assertThat(icon.getAttribute("class"), is("icon-sm"));
                assertThat(badge.getAttribute("style"), is(step.getStyle()));
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

                assertThat(badge.getTagName(), is("a"));

                assertThat(badge.getTextContent(), is(step.getText()));
                assertThat(badge.getAttribute("class"), is(step.getCssClass()));
                assertThat(badge.getAttribute("style"), is(step.getStyle()));
                assertThat(badge.getAttribute("href"), is(step.getLink()));
                assertThat(badge.getAttribute("target"), is(step.getTarget()));
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

                assertThat(badge.getTagName(), is("span"));

                assertThat(badge.getTextContent(), is(step.getText()));
                assertThat(badge.getAttribute("class"), is(step.getCssClass()));
                assertThat(badge.getAttribute("style"), is(step.getStyle()));
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

                assertThat(icon.getTagName(), is("svg"));
                assertThat(badge.getTagName(), is("span"));

                assertThat(icon.getAttribute("data-html-tooltip"), is(step.getText()));
                assertThat(icon.getAttribute("class"), is("icon-sm"));
                assertThat(badge.getAttribute("style"), is(step.getStyle()));
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

                assertThat(icon.getTagName(), is("svg"));
                assertThat(badge.getTagName(), is("span"));

                assertThat(icon.getAttribute("data-html-tooltip"), is(step.getText()));
                assertThat(icon.getAttribute("class"), is("icon-sm"));
                assertThat(badge.getAttribute("style"), is(step.getStyle()));
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

                assertThat(icon.getTagName(), is("svg"));
                assertThat(badge.getTagName(), is("span"));

                assertThat(icon.getAttribute("data-html-tooltip"), is(step.getText()));
                assertThat(icon.getAttribute("class"), is("icon-sm"));
                assertThat(badge.getAttribute("style"), is(step.getStyle()));
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
                assertThat(badge.getElementsByTagName("span"), empty());
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

                assertThat(overview.getElementsByTagName("tr"), hasSize(2));

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement link = summary.getLastElementChild().getFirstElementChild();
                DomElement text = link.getFirstElementChild();

                assertThat(icon.getTagName(), is("svg"));
                assertThat(link.getTagName(), is("a"));
                assertThat(text.getTagName(), is("span"));

                assertThat(text.getTextContent(), is(step.getText()));
                assertThat(link.getAttribute("class"), is(step.getCssClass()));
                assertThat(link.getAttribute("style"), is(step.getStyle()));
                assertThat(link.getAttribute("href"), is(step.getLink()));
                assertThat(link.getAttribute("target"), is(step.getTarget()));
            }
        }

        @Test
        void iconWithTextWithoutLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null, "symbol-rocket plugin-ionicons-api", "Test Text", "Test Class", "Test Style", null, null);
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertThat(overview.getElementsByTagName("tr"), hasSize(2));

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement text = summary.getLastElementChild().getFirstElementChild();

                assertThat(icon.getTagName(), is("svg"));
                assertThat(text.getTagName(), is("span"));

                assertThat(text.getTextContent(), is(step.getText()));
                assertThat(text.getAttribute("class"), is(step.getCssClass()));
                assertThat(text.getAttribute("style"), is(step.getStyle()));
            }
        }

        @Test
        void iconWithoutTextWithoutLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null, "symbol-rocket plugin-ionicons-api", null, "Test Class", "Test Style", null, null);
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertThat(overview.getElementsByTagName("tr"), hasSize(2));

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement text = summary.getLastElementChild().getFirstElementChild();

                assertThat(icon.getTagName(), is("svg"));
                assertThat(text.getTagName(), is("span"));

                assertThat(text.getTextContent(), blankString());
                assertThat(text.getAttribute("class"), is(step.getCssClass()));
                assertThat(text.getAttribute("style"), is(step.getStyle()));
            }
        }

        @Test
        void textWithoutIconWithLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(
                    null, null, "Test Text", "Test Class", "Test Style", "https://jenkins.io", "_blank");
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertThat(overview.getElementsByTagName("tr"), hasSize(2));

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement link = summary.getLastElementChild().getFirstElementChild();
                DomElement text = link.getFirstElementChild();

                assertThat(icon.getTagName(), is("img"));
                assertThat(link.getTagName(), is("a"));
                assertThat(text.getTagName(), is("span"));

                assertThat(
                        icon.getAttribute("src"), is("/jenkins" + Jenkins.RESOURCE_PATH + "/images/16x16/empty.png"));
                assertThat(text.getTextContent(), is(step.getText()));
                assertThat(link.getAttribute("class"), is(step.getCssClass()));
                assertThat(link.getAttribute("style"), is(step.getStyle()));
                assertThat(link.getAttribute("href"), is(step.getLink()));
                assertThat(link.getAttribute("target"), is(step.getTarget()));
            }
        }

        @Test
        void textWithoutIconWithoutLink() throws Throwable {
            AddSummaryStep step = new AddSummaryStep(null, null, "Test Text", "Test Class", "Test Style", null, null);
            WorkflowJob job = runJob(step, null);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(job.getLastBuild());

                assertThat(overview.getElementsByTagName("tr"), hasSize(2));

                DomElement summary = overview.getElementsByTagName("tr").get(0);
                DomElement icon = summary.getFirstElementChild().getFirstElementChild();
                DomElement text = summary.getLastElementChild().getFirstElementChild();

                assertThat(icon.getTagName(), is("img"));
                assertThat(text.getTagName(), is("span"));

                assertThat(
                        icon.getAttribute("src"), is("/jenkins" + Jenkins.RESOURCE_PATH + "/images/16x16/empty.png"));
                assertThat(text.getTextContent(), is(step.getText()));
                assertThat(text.getAttribute("class"), is(step.getCssClass()));
                assertThat(text.getAttribute("style"), is(step.getStyle()));
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

                assertThat(overview.getElementsByTagName("tr"), hasSize(1));
            }
        }
    }

    private static WorkflowJob runJob(AbstractAddBadgeStep addStep, AbstractRemoveBadgesStep removeStep)
            throws Exception {
        WorkflowJob project = r.createProject(WorkflowJob.class);

        String script = """
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
                        """.formatted(addStep.toString(), removeStep != null ? removeStep.toString() : "");

        project.setDefinition(new CpsFlowDefinition(script, true));
        r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        return project;
    }
}
