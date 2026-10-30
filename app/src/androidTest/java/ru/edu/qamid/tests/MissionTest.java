package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Epic;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.data.Credentials;
import ru.edu.qamid.screens.MissionScreen;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Наша миссия")
public class MissionTest extends BaseUiTest {
    @Test
    @DisplayName("TC-MISSION-001: Просмотр, раскрытие и сворачивание цитаты")
    @Description("После самостоятельного входа открывается раздел миссии; текст цитаты раскрывается и скрывается.")
    public void missionQuoteCanBeExpandedAndCollapsed() {

        auth.assertDisplayed();
        auth.enterCredentials(Credentials.validLogin(), Credentials.validPassword());
        auth.submit();
        main.assertDisplayed();
        MissionScreen mission = new MissionScreen();
        main.openMission();
        mission.assertDisplayed();
        mission.clickFirstQuote();
        mission.assertFirstQuoteExpanded();

        mission.clickFirstQuote();
        mission.assertFirstQuoteCollapsed();
    }
}
