package ru.edu.qamid.utils;

import static org.junit.Assert.assertTrue;

import android.app.UiAutomation;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.concurrent.TimeoutException;

public class ToastAssertions {
    private static final long TIMEOUT_MS = 15_000;

    private ToastAssertions() {
    }

    public static void duringAction(int messageResource, Runnable action) {

        String expected = InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getString(messageResource);
        UiAutomation automation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        String appPackage = InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getPackageName();
        try {

            AccessibilityEvent event = automation.executeAndWaitForEvent(action, candidate ->
                    candidate.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
                            && TextUtils.equals("android.widget.Toast", candidate.getClassName())
                            && TextUtils.equals(appPackage, candidate.getPackageName()), TIMEOUT_MS);
            try {

                boolean messageFound = false;
                for (CharSequence text : event.getText()) {
                    if (TextUtils.equals(expected, text)) {
                        messageFound = true;
                    }
                }
                assertTrue("Ожидалось Toast-сообщение: «" + expected
                                + "». Фактически: " + event.getText(),
                        messageFound);
            } finally {

                event.recycle();
            }
        } catch (TimeoutException error) {

            throw new AssertionError("Toast приложения не появился. Ожидалось: «" + expected + "»");
        }
    }
}
