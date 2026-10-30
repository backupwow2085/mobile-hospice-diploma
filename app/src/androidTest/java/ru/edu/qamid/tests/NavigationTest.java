package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.*;
import io.qameta.allure.kotlin.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.data.Credentials;
import ru.edu.qamid.screens.NewsScreen;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Навигация")
public class NavigationTest extends BaseUiTest {
    @Test
    @DisplayName("TC-NAV-001: Main — News — Main через меню")
    @Description("Текущий раздел недоступен в меню; оба перехода открывают нужный экран.")
    public void mainAndNewsMenuNavigation() {
        auth.enterCredentials(Credentials.validLogin(), Credentials.validPassword());
        auth.submit();
        main.assertDisplayed();
        main.openMenu();
        main.assertMainMenuItemDisabled();
        main.selectNews();
        NewsScreen news = new NewsScreen();
        news.assertDisplayed();
        main.openMenu();
        news.assertNewsMenuItemDisabled();
        news.selectMainFromMenu();
        main.assertDisplayed();
    }
}
