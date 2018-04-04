package com.jenkinsci.plugins.badge.action;

import hudson.model.Action;
import hudson.model.BuildBadgeAction;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClassHierarchyTest {

  @Test
  public void badgeSummaryAction() {
    assertTrue(Action.class.isAssignableFrom(BadgeSummaryAction.class));
    assertTrue(AbstractAction.class.isAssignableFrom(BadgeSummaryAction.class));

    assertFalse(BuildBadgeAction.class.isAssignableFrom(BadgeSummaryAction.class));
    assertFalse(AbstractBadgeAction.class.isAssignableFrom(BadgeSummaryAction.class));
  }

  @Test
  public void badgeAction() {
    assertTrue(Action.class.isAssignableFrom(BadgeAction.class));
    assertTrue(AbstractAction.class.isAssignableFrom(BadgeAction.class));

    assertTrue(BuildBadgeAction.class.isAssignableFrom(BadgeAction.class));
    assertTrue(BuildBadgeAction.class.isAssignableFrom(BadgeAction.class));
  }

  @Test
  public void htmlBadgeAction() {
    assertTrue(Action.class.isAssignableFrom(HtmlBadgeAction.class));
    assertTrue(AbstractAction.class.isAssignableFrom(HtmlBadgeAction.class));

    assertTrue(BuildBadgeAction.class.isAssignableFrom(HtmlBadgeAction.class));
    assertTrue(BuildBadgeAction.class.isAssignableFrom(HtmlBadgeAction.class));
  }

}