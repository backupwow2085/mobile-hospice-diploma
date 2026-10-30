package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Epic;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.R;
import ru.edu.qamid.data.Credentials;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Авторизация")
public class AuthTest extends BaseUiTest {
    @Test
    @DisplayName("TC-AUTH-001: Вход с корректными данными")
    @Description("Корректные учётные данные открывают главный экран с логотипом и блоком новостей.")
    public void validCredentialsOpenMainScreen() {

        auth.assertDisplayed();
        auth.enterCredentials(Credentials.validLogin(), Credentials.validPassword());
        auth.submit();
        main.assertDisplayed();
    }

    @Test
    @DisplayName("TC-AUTH-002: Вход с пустыми полями отклоняется")
    @Description("При пустых полях показано сообщение о необходимости логина и пароля; форма авторизации остаётся на экране, главный экран не открывается.")
    public void emptyCredentialsShowValidationAndKeepAuthorizationScreen() {

        auth.assertDisplayed();
        auth.enterCredentials("", "");
        auth.submitAndAssertToast(R.string.empty_login_or_password);
        auth.assertDisplayed();
    }

    @Test
    @DisplayName("TC-AUTH-005: Вход с неверными данными отклоняется")
    @Description("Неверные данные должны вызвать сообщение о неверном логине/пароле; форма авторизации остаётся на экране, главный экран не открывается. Известное расхождение: BUG-003, см. bugs/BUG-003-invalid-login-message.md.")
    public void invalidCredentialsShowErrorAndKeepAuthorizationScreen() {

        auth.assertDisplayed();
        auth.enterCredentials(Credentials.INVALID_LOGIN, Credentials.INVALID_PASSWORD);
        auth.submitAndAssertToast(R.string.wrong_login_or_password);
        auth.assertDisplayed();
    }
}
