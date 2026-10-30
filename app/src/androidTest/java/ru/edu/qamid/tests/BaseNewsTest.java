package ru.edu.qamid.tests;

import org.junit.Before;
import java.time.LocalDate;
import ru.edu.qamid.data.Credentials;
import ru.edu.qamid.data.NewsData;
import ru.edu.qamid.screens.*;

public abstract class BaseNewsTest extends BaseUiTest {
    protected final NewsScreen news = new NewsScreen();
    protected final ControlPanelScreen panel = new ControlPanelScreen();
    protected final NewsFormScreen form = new NewsFormScreen();
    protected final NewsFilterScreen filter = new NewsFilterScreen();

    @Before
    public void signInAndOpenPanel() {

        auth.enterCredentials(Credentials.validLogin(), Credentials.validPassword());
        auth.submit();
        main.assertDisplayed();
        main.openNews();
        news.assertDisplayed();
        news.openControlPanel();
        panel.assertDisplayed();
    }

    protected NewsData createNews(String label, int categoryIndex) {
        return createNews(label, categoryIndex, LocalDate.now());
    }

    protected NewsData createNews(String label, int categoryIndex, LocalDate date) {
        NewsData data = new NewsData(label, date);

        panel.openCreate();
        form.assertDisplayed();
        form.fill(data, categoryIndex);
        form.save();
        panel.assertDisplayed();
        panel.assertContains(data.title);
        return data;
    }

    protected void deactivate(NewsData data) {
        panel.openEdit(data.title);
        form.assertDisplayed();
        form.deactivate();
        form.save();
        panel.assertDisplayed();
        panel.assertInactive(data.title);
    }

    protected void openPublicNews() {

        panel.returnToNews();
        news.assertDisplayed();
    }

}
