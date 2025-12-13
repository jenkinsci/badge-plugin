package com.jenkinsci.plugins.badge.decorator;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import hudson.model.PageDecorator;
import org.junit.jupiter.api.Test;

class BadgePageDecoratorTest {

    @Test
    void badgePageDecorator() {
        assertInstanceOf(PageDecorator.class, new BadgePageDecorator());
    }
}
