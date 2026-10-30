package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Epic;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.data.Credentials;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Выход из учётной записи")
public class LogoutTest extends BaseUiTest {
    @Test
    @DisplayName("TC-LOGOUT-001: После выхода главный экран недоступен")
    @Description("Тест выполняет вход и выходит через меню. Форма авторизации показана; кнопка «Назад» не открывает главный экран; после повторного открытия окна снова требуется вход.")
    public void logoutPreventsReturningToMainScreen() {

        auth.assertDisplayed();
        auth.enterCredentials(Credentials.validLogin(), Credentials.validPassword());
        auth.submit();
        main.assertDisplayed();
        main.logOut();
        auth.assertDisplayed();

        auth.assertBackDoesNotOpenMain();

        reopenApp();
        auth.assertDisplayed();
    }
}
