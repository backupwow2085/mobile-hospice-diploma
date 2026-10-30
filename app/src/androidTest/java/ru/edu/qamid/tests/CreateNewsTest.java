package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.*;
import io.qameta.allure.kotlin.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.data.NewsData;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Создание новости")
public class CreateNewsTest extends BaseNewsTest {
    @Test
    @DisplayName("TC-CREATE-001: Создание активной новости")
    @Description("Новая запись содержит введённые заголовок, описание, категорию, дату и время; статус Active.")
    public void createNewsWithRequiredFields() {
        NewsData data = createNews("create", 0);
        panel.assertContains(data.title);
        panel.assertDetails(data);

        panel.openEdit(data.title);
        form.assertDisplayed();
        form.assertValues(data);
    }

    @Test
    @DisplayName("TC-CREATE-002: Пустая форма не сохраняется")
    @Description("Toast Fill empty fields, ошибки всех обязательных полей, форма остаётся открытой.")
    public void emptyFormShowsRequiredFieldErrors() {
        panel.openCreate();
        form.assertDisplayed();
        form.assertEmptyFormValidation();
    }

    @Test
    @DisplayName("TC-CREATE-005: Отказ от выхода сохраняет введённые данные")
    @Description("После Cancel в предупреждении форма остаётся открытой со всеми введёнными значениями.")
    public void cancelDiscardKeepsFormValues() {
        NewsData draft = new NewsData("keep_draft");
        panel.openCreate();
        form.assertDisplayed();
        form.fill(draft, 0);
        form.cancel();
        form.continueEditing();
        form.assertDisplayed();
        form.assertValues(draft);
    }

    @Test
    @DisplayName("TC-CREATE-006: Подтверждение выхода не создаёт новость")
    @Description("После OK форма закрывается, уникальный заголовок отсутствует в текущем списке; контрольная запись остаётся. Повторная загрузка не проверяется (BUG-005).")
    public void confirmDiscardDoesNotCreateNews() {
        NewsData remaining = createNews("discard_control", 0);
        NewsData draft = new NewsData("discard");
        panel.openCreate();
        form.assertDisplayed();
        form.fill(draft, 0);
        form.cancel();
        form.discardChanges();
        panel.assertDisplayed();
        panel.assertContains(remaining.title);
        panel.assertAbsent(draft.title);
    }
}
