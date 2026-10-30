package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.*;
import io.qameta.allure.kotlin.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.data.NewsData;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Панель управления: фильтр активности")
public class ControlPanelFilterTest extends BaseNewsTest {
    @Test
    @DisplayName("TC-CTRL-002: Фильтр Active исключает неактивные новости")
    @Description("Создаются свои новости двух статусов; все результаты фильтра активны, неактивная запись исключена.")
    public void activeFilterExcludesInactiveNews() {
        NewsData active = createNews("active_filter", 0);
        NewsData inactive = createNews("inactive_control", 0);
        deactivate(inactive);
        panel.openFilter();
        filter.assertDisplayed();
        filter.onlyActive();
        filter.apply();
        panel.assertDisplayed();
        panel.assertOnlyActive(active.title, inactive.title);
    }

    @Test
    @DisplayName("TC-CTRL-003: Фильтр Not active исключает активные новости")
    @Description("Создаются свои новости двух статусов; все результаты фильтра неактивны, активная запись исключена.")
    public void inactiveFilterExcludesActiveNews() {
        NewsData active = createNews("active_control", 0);
        NewsData inactive = createNews("inactive_filter", 0);
        deactivate(inactive);
        panel.openFilter();
        filter.assertDisplayed();
        filter.onlyInactive();
        filter.apply();
        panel.assertDisplayed();
        panel.assertOnlyInactive(inactive.title, active.title);
    }
}
