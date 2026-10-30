package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.*;
import io.qameta.allure.kotlin.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.data.NewsData;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Редактирование новости")
public class EditNewsTest extends BaseNewsTest {
    @Test
    @DisplayName("TC-EDIT-002: Изменение заголовка и описания")
    @Description("Новый текст сохраняется; категория, дата, время и активность не меняются.")
    public void editTitleAndDescriptionKeepsOtherValues() {
        NewsData data = createNews("edit", 0);
        String oldTitle = data.title;
        panel.openEdit(oldTitle);
        form.assertDisplayed();
        data.title += "_changed";
        data.description = "Изменённое описание " + data.title;
        form.changeTitleAndDescription(data);
        form.save();
        panel.assertDisplayed();
        panel.assertContains(data.title);
        panel.assertAbsent(oldTitle);
        panel.assertDetails(data);
        panel.openEdit(data.title);
        form.assertDisplayed();
        form.assertValues(data);
    }

    @Test
    @DisplayName("TC-EDIT-003: Изменение Active на Not active")
    @Description("Переключатель меняет подпись, после сохранения карточка содержит Not active.")
    public void deactivatedNewsKeepsInactiveStatus() {
        NewsData data = createNews("inactive", 0);
        deactivate(data);

        panel.assertInactive(data.title);
    }
}
