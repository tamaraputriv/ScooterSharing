package dk.itu.moapd.scootersharing

import android.view.View
import android.view.ViewGroup
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dk.itu.moapd.scootersharing.activities.ScooterSharingActivity
import androidx.lifecycle.Lifecycle.State
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ScooterSharingActivityTest {

    @get:Rule
    var sActivityScenarioRule = ActivityScenarioRule(ScooterSharingActivity::class.java)

    @Test
    fun mainActivityTest_logout() {
        Thread.sleep(700)
        val overflowMenuButton = Espresso.onView(
            Matchers.allOf(
                withContentDescription("More options"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.topAppBar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        overflowMenuButton.perform(click())
        Thread.sleep(700)

        val materialTextView = Espresso.onView(
            Matchers.allOf(
                withId(androidx.appcompat.R.id.title), withText("Sign out"),
                childAtPosition(
                    childAtPosition(
                        withId(androidx.appcompat.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())
        Thread.sleep(700)

    }

    @Test
    fun mainActivityTest_createMainActivity() {
        val scenario = sActivityScenarioRule.scenario
        scenario.moveToState(State.RESUMED)
    }

    @Test
    fun mainActivityTest_openMap() {

        Thread.sleep(700)
        val mapButton = Espresso.onView(
            Matchers.allOf(
                withText("Map"),
            )
        )

        mapButton.perform(scrollTo(), click())

        Thread.sleep(300)

    }

    @Test
    fun mainActivityTest_openCurrentRide() {

        Thread.sleep(700)
        val rideButton = Espresso.onView(
            Matchers.allOf(
                withText("Ride"),
            )
        )

        rideButton.perform(scrollTo(), click())

        Thread.sleep(300)

        val rideFragmentHeader = Espresso.onView(
            Matchers.allOf(
                withId(R.id.current_scooter_name), withText("Current scooter model"),
                isDisplayed()
            )
        )

        rideFragmentHeader.check(matches(withText("Current scooter model")))

        Thread.sleep(300)

        val rideFragmentStartButton = Espresso.onView(
            Matchers.allOf(
                withId(R.id.start_ride_button), withText("Start ride"),
                isDisplayed()
            )
        )

        rideFragmentStartButton.check(matches(isDisplayed()))
        rideFragmentStartButton.perform(click())

        Thread.sleep(300)
    }

    @Test
    fun mainActivityTest_openCamera() {
        Thread.sleep(700)
        val cameraButton = Espresso.onView(
            Matchers.allOf(
                withText("Camera"),
            )
        )

        cameraButton.perform(scrollTo(), click())
        Thread.sleep(300)

        val cameraFragmentCaptureButton = Espresso.onView(
            Matchers.allOf(
                withId(R.id.camera_capture_button), withContentDescription("Take picture"),
                isDisplayed()
            )
        )

        cameraFragmentCaptureButton.check(matches(isDisplayed()))
        cameraFragmentCaptureButton.perform(click())

        Thread.sleep(300)
    }

    @Test
    fun mainActivityTest_openListOfRides() {
        Thread.sleep(700)

        val ridesButton = Espresso.onView(
            Matchers.allOf(
                withText("Rides"),
            )
        )

        ridesButton.perform(scrollTo(), click())
        Thread.sleep(300)
        val ridesRecyclerView = Espresso.onView(
            Matchers.allOf(
                withId(R.id.rides_recycler_view)
            )
        )
        Thread.sleep(700)
        ridesRecyclerView.perform(swipeUp())
        Thread.sleep(300)
    }

    @Test
    fun mainActivityTest_openUserTab() {
        Thread.sleep(700)

        val userButton = Espresso.onView(
            Matchers.allOf(
                withText("User"),
            )
        )

        userButton.perform(scrollTo(), click())
        Thread.sleep(700)
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

}