package com.zoromatic.widgets;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JavaUnitTest {

    @Test
    public void testUnitTest() {
        float actual = 200;
        // expected value is 212
        float expected = 212;
        // use this method because float is not precise
        assertEquals("Valued are different", expected, actual, 0.001);
    }

}