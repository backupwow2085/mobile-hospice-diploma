package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import android.view.View;
import org.hamcrest.Matcher;
import io.qameta.allure.kotlin.Allure;
import kotlin.Unit;
import ru.edu.qamid.R;
import ru.edu.qamid.utils.WaitForView;

public class MissionScreen {

    private final Matcher<View> firstCard = allOf(
            withId(R.id.our_mission_item_material_card_view),
            hasDescendant(withText(startsWith("«Хоспис для меня"))));

    private final Matcher<View> firstDescription = allOf(
            withId(R.id.our_mission_item_description_text_view), isDescendantOfA(firstCard));

    public void assertDisplayed() {
        Allure.step("Проверить заголовок раздела и список цитат", step -> {
            WaitForView.waitUntil(allOf(firstCard, isDisplayed()));

            onView(withId(R.id.our_mission_title_text_view))
                    .check(matches(allOf(isDisplayed(), withText(R.string.our_mission_title_text))));
            assertFirstQuoteCollapsed();
            return Unit.INSTANCE;
        });
    }

    public void clickFirstQuote() {
        Allure.step("Нажать на первую цитату", step -> {

            Matcher<View> visibleCard = allOf(firstCard, isDisplayed());
            WaitForView.waitUntil(visibleCard);
            onView(visibleCard).perform(click());
            return Unit.INSTANCE;
        });
    }

    public void assertFirstQuoteExpanded() {
        Allure.step("Проверить видимый непустой текст цитаты", step -> {

            Matcher<View> visibleDescription = allOf(firstDescription, isDisplayed(), not(withText("")));
            WaitForView.waitUntil(visibleDescription);
            onView(visibleDescription).check(matches(allOf(isDisplayed(), not(withText("")))));
            return Unit.INSTANCE;
        });
    }

    public void assertFirstQuoteCollapsed() {
        Allure.step("Проверить, что текст первой цитаты скрыт", step -> {

            Matcher<View> hiddenDescription = allOf(firstDescription, withEffectiveVisibility(GONE));
            WaitForView.waitUntil(hiddenDescription);
            onView(hiddenDescription).check(matches(withEffectiveVisibility(GONE)));
            return Unit.INSTANCE;
        });
    }
}
