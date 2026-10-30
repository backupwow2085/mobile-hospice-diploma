package ru.edu.qamid.tests;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.*;
import io.qameta.allure.kotlin.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.edu.qamid.data.NewsData;

@RunWith(AllureAndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Удаление новости")
public class DeleteNewsTest extends BaseNewsTest {
    @Test
    @DisplayName("TC-DELETE-001: Отмена удаления сохраняет новость")
    @Description("Cancel закрывает предупреждение; собственная тестовая новость остаётся в панели.")
    public void cancelDeletionKeepsNews() {
        NewsData data = createNews("cancel_delete", 0);
        panel.requestDelete(data.title);
        panel.cancelDelete();
        panel.assertContains(data.title);
    }

    @Test
    @DisplayName("TC-DELETE-002: Подтверждение удаления убирает новость")
    @Description("После OK своя запись отсутствует, контрольная остаётся в текущем списке. Повторная загрузка с сервера не проверяется (BUG-005).")
    public void confirmedDeletionRemovesNewsFromCurrentList() {
        NewsData remaining = createNews("delete_control", 0);
        NewsData data = createNews("delete", 0);
        panel.requestDelete(data.title);
        panel.confirmDelete();
        panel.assertDeletedWithControl(data.title, remaining.title);
    }
}
