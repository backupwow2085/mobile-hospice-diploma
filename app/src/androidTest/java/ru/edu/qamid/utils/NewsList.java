package ru.edu.qamid.utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.*;

import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.hamcrest.Matcher;
import ru.edu.qamid.R;

public class NewsList {
    private static final long TIMEOUT_MS = 15_000;

    private NewsList() { }

    public static Matcher<View> card(String title) {
        return allOf(withId(R.id.news_item_material_card_view), hasDescendant(withText(title)));
    }

    public static Matcher<View> field(String title, int id) {
        return allOf(withId(id), isDescendantOfA(card(title)), isDisplayed());
    }

    public static void scrollTo(String title) {
        onView(withId(R.id.news_list_recycler_view)).perform(new ViewAction() {
            public Matcher<View> getConstraints() { return isAssignableFrom(RecyclerView.class); }
            public String getDescription() { return "Дождаться и прокрутить к новости " + title; }
            public void perform(UiController ui, View view) {
                long deadline = SystemClock.uptimeMillis() + TIMEOUT_MS;
                PerformException lastError;
                do {
                    try {
                        RecyclerViewActions.scrollTo(card(title)).perform(ui, view);
                        return;
                    } catch (PerformException notLoadedYet) {
                        if (!isMissingNews(notLoadedYet)) throw notLoadedYet;
                        lastError = notLoadedYet;
                        ui.loopMainThreadForAtLeast(50);
                    }
                } while (SystemClock.uptimeMillis() < deadline);
                throw lastError;
            }
        });
        WaitForView.waitUntil(allOf(card(title), isDisplayed()));
    }

    public static class Row {
        public String title;
        public String date;
        public String status;
    }

    public static List<Row> readAll() {
        waitForList();
        TreeMap<Integer, Row> rows = new TreeMap<>();
        onView(withId(R.id.news_list_recycler_view)).perform(new ViewAction() {
            public Matcher<View> getConstraints() { return isAssignableFrom(RecyclerView.class); }
            public String getDescription() { return "Прочитать все новости с прокруткой"; }
            public void perform(UiController ui, View view) {
                RecyclerView list = (RecyclerView) view;
                long deadline = SystemClock.uptimeMillis() + TIMEOUT_MS;
                list.scrollToPosition(0);
                ui.loopMainThreadUntilIdle();

                while (true) {
                    readVisibleRows(list, rows);
                    if (!list.canScrollVertically(1)) break;
                    if (SystemClock.uptimeMillis() >= deadline) {
                        throw new AssertionError("За 15 секунд не удалось просмотреть весь список новостей");
                    }
                    list.scrollBy(0, Math.max(1, list.getHeight() / 2));
                    ui.loopMainThreadForAtLeast(50);
                }

                assertCompleteList(list, rows);
            }
        });
        return new ArrayList<>(rows.values());
    }

    private static void waitForList() {
        WaitForView.waitUntil(anyOf(
                allOf(withId(R.id.news_list_recycler_view), isDisplayed(), hasDescendant(withId(R.id.news_item_title_text_view))),
                allOf(withId(R.id.control_panel_empty_news_list_text_view), isDisplayed(), withText(R.string.empty_claim_list_text)),
                allOf(withId(R.id.empty_news_list_text_view), isDisplayed(), withText(R.string.empty_claim_list_text))));
    }

    private static void readVisibleRows(RecyclerView list, TreeMap<Integer, Row> rows) {
        for (int i = 0; i < list.getChildCount(); i++) {
            View card = list.getChildAt(i);
            int position = list.getChildAdapterPosition(card);
            assertTrue("У карточки нет позиции в списке", position != RecyclerView.NO_POSITION);
            rows.put(position, readRow(card));
        }
    }

    private static Row readRow(View card) {
        Row row = new Row();
        row.title = text(card, R.id.news_item_title_text_view);
        row.date = text(card, R.id.news_item_date_text_view);
        if (row.date.isEmpty()) row.date = text(card, R.id.news_item_publication_date_text_view);
        row.status = text(card, R.id.news_item_published_text_view);
        assertFalse("У карточки пустой заголовок", row.title.trim().isEmpty());
        assertFalse("У карточки пустая дата", row.date.trim().isEmpty());
        return row;
    }

    private static void assertCompleteList(RecyclerView list, TreeMap<Integer, Row> rows) {
        if (rows.isEmpty()) {
            assertEmptyMessage(list.getRootView());
            return;
        }
        int expectedPosition = 0;
        for (int position : rows.keySet()) {
            assertEquals("При прокрутке пропущена карточка", expectedPosition++, position);
        }
        assertEquals("Прочитаны не все карточки списка", list.getAdapter().getItemCount(), rows.size());
    }

    private static void assertEmptyMessage(View root) {
        TextView empty = root.findViewById(R.id.control_panel_empty_news_list_text_view);
        if (empty == null) empty = root.findViewById(R.id.empty_news_list_text_view);
        assertNotNull("Нет карточек и сообщения о пустом списке", empty);
        assertTrue("Пустота списка не подтверждена интерфейсом", isDisplayed().matches(empty));
        assertEquals("Вместо пустого списка показана ошибка", root.getResources().getString(R.string.empty_claim_list_text), empty.getText().toString());
    }

    private static boolean isMissingNews(PerformException error) {
        Throwable cause = error;
        while (cause != null) {
            String message = cause.getMessage();
            if (message != null && message.contains("Found 0 items matching holder")) return true;
            cause = cause.getCause();
        }
        return false;
    }

    private static String text(View card, int id) {
        TextView text = card.findViewById(id);
        return text == null ? "" : text.getText().toString();
    }
}
