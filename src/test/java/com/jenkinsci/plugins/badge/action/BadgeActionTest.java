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

  }

}
