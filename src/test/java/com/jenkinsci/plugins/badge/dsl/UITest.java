package com.jenkinsci.plugins.badge.dsl;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.jenkinsci.plugins.badge.tab.SummaryTab;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import jenkins.model.Jenkins;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlDivision;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.FlagExtension;
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
        void iconWithLink() throws Exception {
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
                // copy to clipboard should not be enabled
                assertThat(badge.hasAttribute("data-text"), is(false));
            }
        }

        @Test
        void iconWithoutLink() throws Exception {
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
        void textWithLink() throws Exception {
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
                // copy to clipboard should not be enabled
                assertThat(badge.hasAttribute("data-text"), is(false));
            }
        }

        @Test
        void textWithoutLink() throws Exception {
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
                // copy to clipboard should be enabled
                assertThat(badge.getAttribute("data-text"), is(step.getText()));
            }
        }

        @Test
        void info() throws Exception {
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
                // copy to clipboard should be enabled
                assertThat(badge.getAttribute("data-text"), is(step.getText()));
            }
        }

        @Test
        void warning() throws Exception {
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
                // copy to clipboard should be enabled
                assertThat(badge.getAttribute("data-text"), is(step.getText()));
            }
        }

        @Test
        void error() throws Exception {
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
                // copy to clipboard should be enabled
                assertThat(badge.getAttribute("data-text"), is(step.getText()));
            }
        }

        @Test
        void remove() throws Exception {
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
        void iconWithTextWithLink() throws Exception {
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
        void iconWithTextWithoutLink() throws Exception {
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
        void iconWithoutTextWithoutLink() throws Exception {
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
        void textWithoutIconWithLink() throws Exception {
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
        void textWithoutIconWithoutLink() throws Exception {
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
        void remove() throws Exception {
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

    @Nested
    class Experimental {

        @SuppressWarnings("unused")
        @RegisterExtension
        private final FlagExtension<String> newJobPage =
                FlagExtension.systemProperty("new-job-page.flag.defaultValue", "true");

        @SuppressWarnings("unused")
        @RegisterExtension
        private final FlagExtension<String> newBuildPage =
                FlagExtension.systemProperty("new-build-page.flag.defaultValue", "true");

        @Test
        void badge() throws Exception {
            AddBadgeStep step = new AddBadgeStep(
                    null,
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io",
                    "_blank");
            WorkflowJob job = runJob(step, null);
            WorkflowRun run = job.getLastBuild();

            // new job page
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
                // copy to clipboard should not be enabled
                assertThat(badge.hasAttribute("data-text"), is(false));
            }

            // new build page
            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(run);
                DomElement badge = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#main-panel div.app-build-content div.jenkins-app-bar div.jenkins-app-bar__controls div.jenkins-details a"),
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
                // copy to clipboard should not be enabled
                assertThat(badge.hasAttribute("data-text"), is(false));
            }
        }

        @Test
        void summary() throws Exception {
            AddSummaryStep step = new AddSummaryStep(
                    null,
                    "symbol-rocket plugin-ionicons-api",
                    "Test Text",
                    "Test Class",
                    "Test Style",
                    "https://jenkins.io",
                    "_blank");
            WorkflowJob job = runJob(step, null);
            WorkflowRun run = job.getLastBuild();
            SummaryTab summaryTab = new SummaryTab(run);

            try (JenkinsRule.WebClient webClient = r.createWebClient()) {
                HtmlPage overview = webClient.getPage(run);

                // widget
                DomNodeList<DomNode> widget = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                                "#main-panel div.app-build-content div.app-build__grid div.jenkins-card")
                                        .getNextSibling()
                                        .getChildNodes(),
                                Objects::nonNull);
                DomNode widgetTitle = widget.get(0).getFirstChild();
                assertThat(widgetTitle.getTextContent().trim(), is(summaryTab.getDisplayName()));
                assertThat(
                        widgetTitle.getAttributes().getNamedItem("href").getNodeValue(), is(summaryTab.getUrlName()));

                DomElement widgetSummary = ((HtmlDivision) widget.get(1).getFirstChild())
                        .getElementsByTagName("tr")
                        .get(0);
                DomElement icon = widgetSummary.getFirstElementChild().getFirstElementChild();
                DomElement link = widgetSummary.getLastElementChild().getFirstElementChild();
                DomElement text = link.getFirstElementChild();

                assertThat(icon.getTagName(), is("svg"));
                assertThat(link.getTagName(), is("a"));
                assertThat(text.getTagName(), is("span"));

                assertThat(text.getTextContent(), is(step.getText()));
                assertThat(link.getAttribute("class"), is(step.getCssClass()));
                assertThat(link.getAttribute("style"), is(step.getStyle()));
                assertThat(link.getAttribute("href"), is(step.getLink()));
                assertThat(link.getAttribute("target"), is(step.getTarget()));

                // tab
                DomElement tabs = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> overview.querySelector(
                                        "#main-panel div.app-build-bar div.app-build-bar__tabs div.app-build-tabs"),
                                Objects::nonNull);

                assertThat(tabs.getElementsByTagName("a"), hasSize(3));

                DomElement tabButton = tabs.getElementsByTagName("a").get(2);
                assertThat(tabButton.getTextContent().trim(), is(summaryTab.getDisplayName()));
                assertThat(tabButton.getAttribute("href"), endsWith("/" + run.getUrl() + summaryTab.getUrlName()));
                assertThat(tabButton.getFirstElementChild().getTagName(), is("svg"));

                HtmlPage tab = tabButton.click();
                DomElement tabTitle = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> tab.querySelector(
                                        "#main-panel div.app-build-content div.jenkins-app-bar div.jenkins-app-bar__content h2"),
                                Objects::nonNull);
                assertThat(tabTitle.getTextContent().trim(), is(summaryTab.getDisplayName()));

                DomElement tabSummary = await().atMost(5, TimeUnit.SECONDS)
                        .until(
                                () -> tab.querySelector("#main-panel div.app-build-content div table tbody tr"),
                                Objects::nonNull);
                icon = tabSummary.getFirstElementChild().getFirstElementChild();
                link = tabSummary.getLastElementChild().getFirstElementChild();
                text = link.getFirstElementChild();

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
