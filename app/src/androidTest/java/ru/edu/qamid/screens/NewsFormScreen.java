package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static org.hamcrest.Matchers.*;

import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.TextView;
import androidx.test.espresso.contrib.PickerActions;
import io.qameta.allure.kotlin.Allure;
import kotlin.Unit;
import ru.edu.qamid.R;
import ru.edu.qamid.data.NewsData;
import ru.edu.qamid.utils.ToastAssertions;
import ru.edu.qamid.utils.WaitForView;

public class NewsFormScreen {
    public void assertDisplayed() {
        Allure.step("Проверить форму новости", step -> {
            WaitForView.waitUntil(allOf(withId(R.id.news_title_edit_text), isDisplayed()));
            onView(withId(R.id.news_save_button)).check(matches(isDisplayed()));
            return Unit.INSTANCE;
        });
    }

    public void fill(NewsData data, int categoryIndex) {
        Allure.step("Заполнить новость «" + data.title + "»: категорию, дату, время и описание", step -> {

            onView(allOf(withId(com.google.android.material.R.id.text_input_end_icon),
                    isDescendantOfA(withId(R.id.news_category_text_input_layout)))).perform(scrollTo(), click());

            onData(anything()).inRoot(isPlatformPopup()).atPosition(categoryIndex)
                    .check((view, error) -> {
                        if (error != null) throw error;
                        data.category = ((TextView) view).getText().toString();
                        org.junit.Assert.assertFalse("Название категории в списке пустое", data.category.trim().isEmpty());
                    }).perform(click());
            onView(withId(R.id.news_category_auto_complete)).check(matches(withText(data.category)));

            onView(withId(R.id.news_title_edit_text)).perform(scrollTo(), replaceText(data.title));
            onView(withId(R.id.news_publish_date_edit_text)).perform(scrollTo(), click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(
                    data.date.getYear(), data.date.getMonthValue(), data.date.getDayOfMonth()));
            onView(withId(android.R.id.button1)).perform(click());
            onView(withId(R.id.news_publish_time_edit_text)).perform(scrollTo(), click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(data.hour, data.minute));
            onView(withId(android.R.id.button1)).perform(click());
            onView(withId(R.id.news_description_edit_text)).perform(scrollTo(), replaceText(data.description));
            return Unit.INSTANCE;
        });
    }

    public void save() {
        Allure.step("Сохранить новость", step -> {
            onView(withId(R.id.news_save_button)).perform(scrollTo(), click());
            return Unit.INSTANCE;
        });
    }

    public void assertEmptyFormValidation() {
        Allure.step("Сохранить пустую форму и проверить обязательные поля", step -> {
            ToastAssertions.duringAction(R.string.empty_fields, this::save);

            onView(allOf(withId(com.google.android.material.R.id.text_input_start_icon),
                    isDescendantOfA(withId(R.id.news_category_text_input_layout))))
                    .perform(scrollTo()).check(matches(isDisplayed()));
            int[] fields = {R.id.news_title_text_input_layout,
                    R.id.news_publish_date_text_input_layout, R.id.news_publish_time_text_input_layout,
                    R.id.news_description_text_input_layout};
            for (int id : fields) {

                onView(allOf(withId(com.google.android.material.R.id.text_input_end_icon),
                        isDescendantOfA(withId(id)))).perform(scrollTo()).check(matches(isDisplayed()));
            }
            assertDisplayed();
            return Unit.INSTANCE;
        });
    }

    public void cancel() {
        Allure.step("Отменить заполнение и проверить предупреждение", step -> {
            onView(withId(R.id.news_cancel_button)).perform(scrollTo(), click());
            onView(withText(R.string.cancellation)).check(matches(isDisplayed()));
            return Unit.INSTANCE;
        });
    }

    public void continueEditing() {
        Allure.step("Отказаться от выхода без сохранения", step -> {
            onView(withId(android.R.id.button2)).perform(click());
            onView(withText(R.string.cancellation)).check(doesNotExist());
            return Unit.INSTANCE;
        });
    }

    public void discardChanges() {
        Allure.step("Подтвердить выход без сохранения", step -> {
            onView(withId(android.R.id.button1)).perform(click());
            onView(withText(R.string.cancellation)).check(doesNotExist());
            return Unit.INSTANCE;
        });
    }

    public void changeTitleAndDescription(NewsData data) {
        Allure.step("Изменить заголовок и описание новости на «" + data.title + "»", step -> {
            onView(withId(R.id.news_title_edit_text)).perform(scrollTo(), replaceText(data.title));
            onView(withId(R.id.news_description_edit_text)).perform(scrollTo(), replaceText(data.description));
            return Unit.INSTANCE;
        });
    }

    public void deactivate() {
        Allure.step("Выключить активность и проверить подпись", step -> {
            onView(withId(R.id.news_active_switch)).perform(scrollTo()).check(matches(isChecked()));
            onView(withId(R.id.news_active_switch)).perform(click())
                    .check(matches(allOf(not(isChecked()), withText(R.string.news_item_not_active))));
            return Unit.INSTANCE;
        });
    }

    public void assertValues(NewsData data) {
        Allure.step("Проверить сохранность всех введённых значений формы", step -> {
            onView(withId(R.id.news_category_auto_complete)).check(matches(withText(data.category)));
            onView(withId(R.id.news_title_edit_text)).check(matches(withText(data.title)));
            onView(withId(R.id.news_publish_date_edit_text)).check(matches(withText(data.dateText())));
            onView(withId(R.id.news_publish_time_edit_text)).check(matches(withText(data.timeText())));
            onView(withId(R.id.news_description_edit_text)).check(matches(withText(data.description)));
            onView(withId(R.id.news_active_switch)).check(matches(isChecked()));
            return Unit.INSTANCE;
        });
    }
}
