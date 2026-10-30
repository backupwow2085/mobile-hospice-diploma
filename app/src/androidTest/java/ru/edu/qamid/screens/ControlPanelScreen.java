package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import androidx.test.platform.app.InstrumentationRegistry;
import io.qameta.allure.kotlin.Allure;
import java.util.List;
import kotlin.Unit;
import ru.edu.qamid.R;
import ru.edu.qamid.data.NewsData;
import ru.edu.qamid.utils.NewsList;
import ru.edu.qamid.utils.WaitForView;

public class ControlPanelScreen {
    public void assertDisplayed() {
        Allure.step("Проверить панель управления", step -> {
            WaitForView.waitUntil(allOf(withId(R.id.add_news_image_view), isDisplayed()));
            onView(withId(R.id.news_control_panel_app_bar)).check(matches(isDisplayed()));
            return Unit.INSTANCE;
        });
    }

    public void openCreate() {
        Allure.step("Открыть создание новости", step -> {
            onView(withId(R.id.add_news_image_view)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void openEdit(String title) {
        Allure.step("Открыть редактирование своей новости", step -> {
            NewsList.scrollTo(title);
            onView(NewsList.field(title, R.id.news_item_edit_image_view)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void openFilter() {
        Allure.step("Открыть фильтр панели управления", step -> {
            onView(withId(R.id.news_filter_button)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void assertContains(String title) {
        Allure.step("Проверить наличие своей новости в панели", step -> {
            NewsList.scrollTo(title);
            onView(NewsList.field(title, R.id.news_item_title_text_view)).check(matches(withText(title)));
            return Unit.INSTANCE;
        });
    }

    public void assertDetails(NewsData data) {
        Allure.step("Проверить заголовок, описание, дату и активность созданной новости", step -> {
            assertContains(data.title);
            onView(NewsList.field(data.title, R.id.news_item_publication_date_text_view)).check(matches(withText(data.dateText())));
            assertActive(data.title);
            onView(NewsList.field(data.title, R.id.news_item_expand_image_view)).perform(click());
            NewsList.scrollTo(data.title);
            WaitForView.waitUntil(NewsList.field(data.title, R.id.news_item_description_text_view));
            onView(NewsList.field(data.title, R.id.news_item_description_text_view)).check(matches(withText(data.description)));
            return Unit.INSTANCE;
        });
    }

    public void assertActive(String title) {
        assertStatus(title, R.string.news_control_panel_active);
    }

    public void assertInactive(String title) {
        assertStatus(title, R.string.news_control_panel_not_active);
    }

    private void assertStatus(String title, int status) {
        Allure.step("Проверить статус своей новости", step -> {
            NewsList.scrollTo(title);
            onView(NewsList.field(title, R.id.news_item_published_text_view)).check(matches(withText(status)));
            return Unit.INSTANCE;
        });
    }

    public void requestDelete(String title) {
        Allure.step("Запросить удаление своей новости и проверить предупреждение", step -> {
            NewsList.scrollTo(title);
            onView(NewsList.field(title, R.id.news_item_delete_image_view)).perform(click());
            onView(withText(R.string.irrevocable_deletion)).check(matches(isDisplayed()));
            return Unit.INSTANCE;
        });
    }

    public void cancelDelete() {
        Allure.step("Отменить удаление новости", step -> {
            onView(withId(android.R.id.button2)).perform(click());
            onView(withText(R.string.irrevocable_deletion)).check(doesNotExist());
            return Unit.INSTANCE;
        });
    }

    public void confirmDelete() {
        Allure.step("Подтвердить удаление новости", step -> {
            onView(withId(android.R.id.button1)).perform(click());
            onView(withText(R.string.irrevocable_deletion)).check(doesNotExist());
            return Unit.INSTANCE;
        });
    }

    public void returnToNews() {
        Allure.step("Вернуться к списку новостей кнопкой Назад без обновления", step -> {
            androidx.test.espresso.Espresso.pressBack();
            return Unit.INSTANCE;
        });
    }

    public boolean contains(String title) {
        return NewsList.readAll().stream().anyMatch(r -> r.title.equals(title));
    }

    public void assertAbsent(String title) {
        Allure.step("Проверить отсутствие новости во всём списке", step -> {
            assertFalse("Новость осталась в списке: " + title, contains(title));
            return Unit.INSTANCE;
        });
    }

    public void assertDeletedWithControl(String deletedTitle, String remainingTitle) {
        Allure.step("Проверить удаление и наличие контрольной новости в одном списке", step -> {
            List<NewsList.Row> rows = NewsList.readAll();
            assertTrue("Контрольная новость недоступна: " + remainingTitle,
                    rows.stream().anyMatch(r -> r.title.equals(remainingTitle)));
            assertFalse("Удалённая новость осталась: " + deletedTitle,
                    rows.stream().anyMatch(r -> r.title.equals(deletedTitle)));
            return Unit.INSTANCE;
        });
    }

    public void assertOnlyActive(String included, String excluded) {
        assertFilteredStatus(R.string.news_control_panel_active, included, excluded);
    }

    public void assertOnlyInactive(String included, String excluded) {
        assertFilteredStatus(R.string.news_control_panel_not_active, included, excluded);
    }

    private void assertFilteredStatus(int statusId, String included, String excluded) {
        Allure.step("Проверить статус всех результатов фильтра", step -> {
            assertContains(included);
            List<NewsList.Row> rows = NewsList.readAll();
            assertTrue("В результатах нет своей подходящей новости", rows.stream().anyMatch(r -> r.title.equals(included)));
            assertFalse("Новость противоположного статуса осталась", rows.stream().anyMatch(r -> r.title.equals(excluded)));
            String expected = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(statusId);
            for (NewsList.Row row : rows) assertEquals("Неверный статус у " + row.title, expected, row.status);
            return Unit.INSTANCE;
        });
    }
}
