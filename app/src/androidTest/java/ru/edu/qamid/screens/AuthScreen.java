package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

import androidx.test.espresso.NoActivityResumedException;
import androidx.test.espresso.NoMatchingViewException;
import io.qameta.allure.kotlin.Allure;
import kotlin.Unit;
import ru.edu.qamid.R;
import ru.edu.qamid.utils.ToastAssertions;
import ru.edu.qamid.utils.WaitForView;

public class AuthScreen {
    public void prepareForTest() {
        Allure.step("Подготовить форму авторизации через интерфейс", step -> {

            WaitForView.waitUntil(anyOf(
                    allOf(withId(R.id.login_edit_text), isDisplayed()),
                    allOf(withId(R.id.main_app_bar), isDisplayed())));
            try {
                onView(withId(R.id.login_edit_text)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException noLoginForm) {

                new MainScreen().logOut();
            }
            assertDisplayed();
            return Unit.INSTANCE;
        });
    }

    public void assertDisplayed() {

        Allure.step("Проверить форму авторизации и отсутствие главного экрана", step -> {

            WaitForView.waitUntil(allOf(withId(R.id.login_edit_text), isDisplayed()));

            onView(withId(R.id.login_edit_text)).check(matches(isDisplayed()));
            onView(withId(R.id.password_edit_text)).check(matches(isDisplayed()));
            onView(withId(R.id.enter_button)).check(matches(isDisplayed()))
                    .check(matches(isEnabled()));
            onView(withId(R.id.main_news_list_container)).check(doesNotExist());

            return Unit.INSTANCE;
        });
    }

    public void assertBackDoesNotOpenMain() {
        Allure.step("Нажать «Назад» и проверить, что главный экран не открылся", step -> {

            pressBackUnconditionally();
            try {
                onView(withId(R.id.main_app_bar)).check(doesNotExist());
                onView(withId(R.id.main_news_list_container)).check(doesNotExist());
            } catch (NoActivityResumedException appClosed) {

            }
            return Unit.INSTANCE;
        });
    }

    public void enterCredentials(String login, String password) {

        Allure.step("Заполнить логин и пароль", step -> {

            onView(withId(R.id.login_edit_text)).perform(replaceText(login));
            onView(withId(R.id.password_edit_text)).perform(replaceText(password), closeSoftKeyboard());
            return Unit.INSTANCE;
        });
    }

    public void submit() {
        Allure.step("Нажать кнопку входа", step -> {

            onView(withId(R.id.enter_button)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void submitAndAssertToast(int expectedMessage) {
        Allure.step("Нажать вход и проверить текст всплывающего сообщения", step -> {

            ToastAssertions.duringAction(expectedMessage, this::submit);
            return Unit.INSTANCE;
        });
    }
}
