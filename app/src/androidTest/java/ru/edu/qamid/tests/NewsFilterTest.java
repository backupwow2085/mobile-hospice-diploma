package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.*;
import io.qameta.allure.kotlin.junit4.DisplayName;
import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.data.NewsData;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Список новостей: сортировка и фильтры")
public class NewsFilterTest extends BaseNewsTest {
    @Test
    @DisplayName("TC-NEWS-001: Сортировка новостей в обоих направлениях")
    @Description("Создаются свои новости за вчера и сегодня; после возврата из панели без перезапуска окна обе должны быть доступны. Проверяется порядок дат всех карточек в обоих направлениях и после повторного нажатия.")
    public void sortingChangesDateOrderBothWays() {
        NewsData earlier = createNews("sort_earlier", 0, LocalDate.now().minusDays(1));
        NewsData later = createNews("sort_later", 0);
        openPublicNews();
        news.assertContains(earlier.title);
        news.assertContains(later.title);
        news.assertSortedDescending();
        news.sort();
        news.assertSortedAscending();
        news.sort();
        news.assertSortedDescending();
    }

    @Test
    @DisplayName("TC-NEWS-002: Фильтр оставляет выбранную категорию")
    @Description("Две свои новости разных категорий видны до фильтра; после него нужная остаётся, другая исключена.")
    public void categoryFilterExcludesOtherCategory() {
        NewsData included = createNews("category_a", 0);
        NewsData excluded = createNews("category_b", 1);
        org.junit.Assert.assertNotEquals("Для фильтра нужны разные категории", included.category, excluded.category);
        openPublicNews();
        news.assertContains(included.title);
        news.assertContains(excluded.title);
        news.openFilter();
        filter.assertDisplayed();
        filter.chooseCategory(included.category);
        filter.apply();
        news.assertDisplayed();
        news.assertCategory(included.category, included.title, excluded.title);
    }

    @Test
    @DisplayName("TC-NEWS-003: Фильтр дат включает граничный день")
    @Description("Создаются свои новости за сегодня и вчера. Интервал сегодня — сегодня включает свою граничную запись, исключает вчерашнюю; проверяются даты всех результатов.")
    public void dateFilterIncludesBoundaryAndExcludesOtherDates() {
        NewsData included = createNews("date", 0);
        NewsData excluded = createNews("date_outside", 0, included.date.minusDays(1));
        openPublicNews();
        news.assertContains(included.title);
        news.assertContains(excluded.title);
        news.openFilter();
        filter.assertDisplayed();
        filter.setStartDate(included.date);
        filter.setEndDate(included.date);
        filter.apply();
        news.assertDisplayed();
        news.assertOnlyDate(included.date, included.title, excluded.title);
    }

    @Test
    @DisplayName("TC-NEWS-004: Неполный интервал не применяется")
    @Description("Заполнено только начало периода; появляется Wrong period, экран фильтра остаётся открыт.")
    public void incompleteDatePeriodShowsDialog() {
        openPublicNews();
        news.openFilter();
        filter.assertDisplayed();
        filter.setStartDate(LocalDate.now());
        filter.apply();
        filter.assertWrongPeriodAndStayOnFilter();
    }
}
