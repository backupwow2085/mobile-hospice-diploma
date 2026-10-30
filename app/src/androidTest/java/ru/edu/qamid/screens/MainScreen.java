package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.anyOf;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;

import io.qameta.allure.kotlin.Allure;
import kotlin.Unit;
import ru.edu.qamid.R;
import ru.edu.qamid.utils.WaitForView;

public class MainScreen {
    public void openMenu() {
        Allure.step("Открыть главное меню", step -> {
            onView(withId(R.id.main_menu_image_button)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void assertMainMenuItemDisabled() {
        Allure.step("Проверить, что пункт Main недоступен на главной", step -> {

            onView(withText(R.string.main)).inRoot(isPlatformPopup()).check(matches(anyOf(
                    not(androidx.test.espresso.matcher.ViewMatchers.isEnabled()),
                    isDescendantOfA(not(androidx.test.espresso.matcher.ViewMatchers.isEnabled())))));
            return Unit.INSTANCE;
        });
    }

    public void selectNews() {
        Allure.step("Выбрать News в главном меню", step -> {
            onView(withText(R.string.news)).inRoot(isPlatformPopup()).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void openNews() {
        openMenu();
        selectNews();
    }

    public void openMain() {
        openMenu();
        Allure.step("Выбрать Main в главном меню", step -> {
            onView(withText(R.string.main)).inRoot(isPlatformPopup()).perform(click());
            return Unit.INSTANCE;
        });
    }
    public void assertDisplayed() {

        Allure.step("Проверить главный экран, логотип и блок новостей", step -> {

            WaitForView.waitUntil(allOf(withId(R.id.main_app_bar), isDisplayed()));
            onView(withId(R.id.main_app_bar)).check(matches(isDisplayed()));

            onView(allOf(withId(R.id.trademark_image_view),
                    isDescendantOfA(withId(R.id.main_app_bar)))).check(matches(isDisplayed()));
            onView(withId(R.id.main_news_list_container)).check(matches(isDisplayed()));
            onView(withId(R.id.enter_button)).check(doesNotExist());
            return Unit.INSTANCE;
        });
    }

    public void openMission() {
        Allure.step("Открыть раздел «Наша миссия»", step -> {

            onView(withId(R.id.our_mission_image_button)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void logOut() {

        Allure.step("Открыть меню пользователя", step -> {
            onView(withId(R.id.authorization_image_button)).perform(click());
            return Unit.INSTANCE;
        });
        Allure.step("Нажать «Выйти»", step -> {
            onView(withText(R.string.log_out)).perform(click());
            return Unit.INSTANCE;
        });
    }
}
