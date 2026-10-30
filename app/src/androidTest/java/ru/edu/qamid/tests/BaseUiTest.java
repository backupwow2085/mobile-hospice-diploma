package ru.edu.qamid.tests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import io.qameta.allure.kotlin.Allure;
import kotlin.Unit;
import org.junit.After;
import org.junit.Before;
import ru.edu.qamid.screens.AuthScreen;
import ru.edu.qamid.screens.MainScreen;

public abstract class BaseUiTest {

    private ActivityScenario<Activity> scenario;

    protected final AuthScreen auth = new AuthScreen();
    protected final MainScreen main = new MainScreen();

    @Before
    public void setUp() {
        launchApp();

        auth.prepareForTest();
    }

    private void launchApp() {

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) {
            throw new AssertionError("Android не нашёл стартовый экран приложения");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        scenario = ActivityScenario.launch(intent);
    }

    protected void reopenApp() {
        Allure.step("Закрыть и повторно открыть окно приложения", step -> {
            if (scenario != null) {
                scenario.close();
            }
            launchApp();

            return Unit.INSTANCE;
        });
    }

    @After
    public void tearDown() {
        if (scenario != null) {

            scenario.close();
        }
    }
}
