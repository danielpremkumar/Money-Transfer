package com.nerdylegend;

import org.junit.Assert;
import org.junit.Test;

public class AppTest {

    @Test
    public void shouldCreateAppObject() {
        // when
        App app = new App();

        // then
        Assert.assertNotNull(app);
    }
}
