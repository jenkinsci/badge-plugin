package com.jenkinsci.plugins.badge.action;


import hudson.model.Hudson;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BadgeActionTest {

  @Rule
  public JenkinsRule r = new JenkinsRule();

  @Test
  public void getIconPath() {
    assertNull(BadgeAction.getIconPath(null));
    assertEquals("/icon.png", BadgeAction.getIconPath("/icon.png"));
    assertEquals("http://foo.com/icon.png", BadgeAction.getIconPath("http://foo.com/icon.png"));
    assertEquals("https://foo.com/icon.png", BadgeAction.getIconPath("https://foo.com/icon.png"));

    assertEquals(Hudson.RESOURCE_PATH + "/images/16x16/http.png", BadgeAction.getIconPath("http.png"));

    assertEquals("symbol-information-circle-outline plugin-ionicons-api", BadgeAction.getIconPath("symbol-information-circle-outline"));
    assertEquals("symbol-information-circle-outline plugin-ionicons-api", BadgeAction.getIconPath("symbol-information-circle-outline plugin-ionicons-api"));
  }

  @Test
  public void getIconClass() {
    BadgeAction action = BadgeAction.createBadge("info.gif", "text");
    assertEquals("", action.getIconClass());
    action = BadgeAction.createBadge("symbol-star", "text");
    assertEquals("icon-sm", action.getIconClass());
    action = BadgeAction.createBadge("symbol-star",  "#000000", "", null);
    assertEquals("icon-sm", action.getIconClass());
    action = BadgeAction.createBadge("symbol-star",  "blue", "", null);
    assertEquals("icon-sm jenkins-!-color-blue", action.getIconClass());
    action = BadgeAction.createBadge("/foo/symbol-star.gif",  "blue", "", null);
    assertEquals("jenkins-!-color-blue", action.getIconClass());
    // teal is not in the palette
    action = BadgeAction.createBadge("symbol-star",  "teal", "", null);
    assertEquals("icon-sm", action.getIconClass());
  }

}
