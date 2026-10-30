package ru.edu.qamid.utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import android.os.SystemClock;
import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.TreeIterables;
import org.hamcrest.Matcher;

public class WaitForView {
    private static final long TIMEOUT_MS = 15_000;

    private WaitForView() {

    }

    public static void waitUntil(Matcher<View> expectedView) {
        long deadline = SystemClock.uptimeMillis() + TIMEOUT_MS;

        boolean[] found = {false};
        do {

            onView(isRoot()).perform(new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return isRoot();
                }

                @Override
                public String getDescription() {
                    return "Дождаться элемента: " + expectedView;
                }

                @Override
                public void perform(UiController uiController, View root) {

                    int matchingViews = 0;
                    for (View view : TreeIterables.breadthFirstViewTraversal(root)) {
                        if (expectedView.matches(view)) {
                            matchingViews++;
                        }
                    }
                    found[0] = matchingViews == 1;

                    if (!found[0]) uiController.loopMainThreadForAtLeast(50);
                }
            });
            if (found[0]) return;
        } while (SystemClock.uptimeMillis() < deadline);

        throw new AssertionError("За 15 секунд не появился ожидаемый элемент: " + expectedView);
    }
}
