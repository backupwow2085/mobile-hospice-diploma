package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import io.qameta.allure.kotlin.Allure;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import kotlin.Unit;
import ru.edu.qamid.R;
import ru.edu.qamid.utils.NewsList;
import ru.edu.qamid.utils.WaitForView;

public class NewsScreen {
    public void assertDisplayed() {
        Allure.step("Проверить полный список новостей", step -> {
            WaitForView.waitUntil(allOf(withId(R.id.news_list_app_bar), isDisplayed()));
            onView(withId(R.id.news_edit_button)).check(matches(isDisplayed()));
            return Unit.INSTANCE;
        });
    }

    public void assertNewsMenuItemDisabled() {
        Allure.step("Проверить, что News недоступен на экране новостей", step -> {
            onView(withText(R.string.news)).inRoot(isPlatformPopup()).check(matches(anyOf(
                    not(isEnabled()), isDescendantOfA(not(isEnabled())))));
            return Unit.INSTANCE;
        });
    }

    public void selectMainFromMenu() {
        Allure.step("Перейти из News на Main", step -> {
            onView(withText(R.string.main)).inRoot(isPlatformPopup()).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void openControlPanel() {
        Allure.step("Открыть панель управления новостями", step -> {
            onView(withId(R.id.news_edit_button)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void openFilter() {
        Allure.step("Открыть фильтр списка новостей", step -> {
            onView(withId(R.id.news_filter_button)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void sort() {
        Allure.step("Переключить направление сортировки новостей", step -> {
            onView(withId(R.id.news_sort_button)).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void assertContains(String title) {
        Allure.step("Проверить наличие своей новости в полном списке", step -> {
            NewsList.scrollTo(title);
            onView(NewsList.field(title, R.id.news_item_title_text_view)).check(matches(withText(title)));
            return Unit.INSTANCE;
        });
    }

    public void assertSortedDescending() {
        Allure.step("Проверить порядок всех новостей от новых к старым", step -> {
            List<NewsList.Row> rows = loadedRows();
            assertTrue("Для проверки сортировки нужны разные даты", rows.stream().map(r -> r.date).distinct().count() >= 2);
            for (int i = 1; i < rows.size(); i++) {
                assertFalse("Нарушен порядок по убыванию дат", date(rows.get(i)).isAfter(date(rows.get(i - 1))));
            }
            return Unit.INSTANCE;
        });
    }

    public void assertSortedAscending() {
        Allure.step("Проверить порядок всех новостей от старых к новым", step -> {
            List<NewsList.Row> rows = loadedRows();
            assertTrue("Для проверки сортировки нужны разные даты", rows.stream().map(r -> r.date).distinct().count() >= 2);
            for (int i = 1; i < rows.size(); i++) {
                assertFalse("Нарушен порядок по возрастанию дат", date(rows.get(i)).isBefore(date(rows.get(i - 1))));
            }
            return Unit.INSTANCE;
        });
    }

    public void assertCategory(String category, String includedTitle, String excludedTitle) {
        Allure.step("Проверить фильтр категории «" + category + "» на двух своих новостях", step -> {
            assertContains(includedTitle);
            List<NewsList.Row> rows = loadedRows();
            assertTrue("В результатах нет своей подходящей новости", rows.stream().anyMatch(r -> r.title.equals(includedTitle)));
            assertFalse("Новость другой категории не должна остаться", rows.stream().anyMatch(r -> r.title.equals(excludedTitle)));

            return Unit.INSTANCE;
        });
    }

    public void assertOnlyDate(LocalDate expectedDate, String includedTitle, String excludedTitle) {
        Allure.step("Проверить включение граничной даты и даты всех результатов", step -> {
            assertContains(includedTitle);
            List<NewsList.Row> rows = loadedRows();
            assertTrue("В результатах нет своей новости на границе", rows.stream().anyMatch(r -> r.title.equals(includedTitle)));
            assertFalse("Своя новость вне интервала осталась в списке", rows.stream().anyMatch(r -> r.title.equals(excludedTitle)));
            for (NewsList.Row row : rows) assertEquals("Новость вне интервала: " + row.title, expectedDate, date(row));
            return Unit.INSTANCE;
        });
    }

    private List<NewsList.Row> loadedRows() {
        List<NewsList.Row> rows = NewsList.readAll();
        assertFalse("Пустой список не подтверждает сортировку или фильтрацию", rows.isEmpty());
        return rows;
    }

    private LocalDate date(NewsList.Row row) {
        return LocalDate.parse(row.date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
