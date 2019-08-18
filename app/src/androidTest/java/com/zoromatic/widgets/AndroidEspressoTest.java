package com.zoromatic.widgets;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class AndroidEspressoTest {


    @Rule
    public ActivityTestRule<LocationsEdit> mActivityRule =
            new ActivityTestRule<>(LocationsEdit.class);

    @Test
    public void ensureTextChangesWork() {
        // Type text and then press the button.
        onView(withId(R.id.title))
                .perform(typeText("HELLO"), closeSoftKeyboard());
        onView(withId(R.id.confirm)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.title)).check(matches(withText("GOOD BYE")));
    }
}