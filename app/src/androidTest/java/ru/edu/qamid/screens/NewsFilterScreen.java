package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static org.hamcrest.Matchers.*;

import android.widget.DatePicker;
import androidx.test.espresso.contrib.PickerActions;
import io.qameta.allure.kotlin.Allure;
import java.time.LocalDate;
import kotlin.Unit;
import ru.edu.qamid.R;
import ru.edu.qamid.utils.WaitForView;

public class NewsFilterScreen {
    public void assertDisplayed() {
        Allure.step("Проверить экран фильтра", step -> {
            WaitForView.waitUntil(allOf(withId(R.id.filter_news_apply_button), isDisplayed()));
            return Unit.INSTANCE;
        });
    }

    public void chooseCategory(String category) {
        Allure.step("Выбрать категорию в фильтре", step -> {
            onView(allOf(withId(com.google.android.material.R.id.text_input_end_icon),
                    isDescendantOfA(withId(R.id.filter_news_category_text_input_layout)))).perform(click());
            onData(equalTo(category)).inRoot(isPlatformPopup()).perform(click());
            onView(withId(R.id.filter_news_category_auto_complete)).check(matches(withText(category)));
            return Unit.INSTANCE;
        });
    }

    public void setStartDate(LocalDate date) {
        Allure.step("Выбрать начало интервала", step -> {
            chooseDate(R.id.filter_news_date_start_edit_text, date);
            return Unit.INSTANCE;
        });
    }

    public void setEndDate(LocalDate date) {
        Allure.step("Выбрать конец интервала", step -> {
            chooseDate(R.id.filter_news_date_end_edit_text, date);
            return Unit.INSTANCE;
        });
    }

    private void chooseDate(int field, LocalDate date) {
        onView(withId(field)).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    public void onlyActive() {
        Allure.step("Оставить только статус Active", step -> {
            onView(withId(R.id.filter_news_active_check_box)).check(matches(isChecked()));
            onView(withId(R.id.filter_news_inactive_check_box)).check(matches(isChecked())).perform(click());
            onView(withId(R.id.filter_news_inactive_check_box)).check(matches(not(isChecked())));
            return Unit.INSTANCE;
        });
    }

    public void onlyInactive() {
        Allure.step("Оставить только статус Not active", step -> {
            onView(withId(R.id.filter_news_inactive_check_box)).check(matches(isChecked()));
            onView(withId(R.id.filter_news_active_check_box)).check(matches(isChecked())).perform(click());
            onView(withId(R.id.filter_news_active_check_box)).check(matches(not(isChecked())));
            return Unit.INSTANCE;
        });
    }

    public void apply() {
        Allure.step("Применить фильтр", step -> {
            onView(withId(R.id.filter_news_apply_button)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void assertWrongPeriodAndStayOnFilter() {
        Allure.step("Проверить диалог неверного периода и сохранение экрана фильтра", step -> {
            onView(withText(R.string.wrong_news_date_period)).check(matches(isDisplayed()));
            onView(withId(android.R.id.button1)).perform(click());
            onView(withText(R.string.wrong_news_date_period)).check(doesNotExist());
            assertDisplayed();
            return Unit.INSTANCE;
        });
    }
}
