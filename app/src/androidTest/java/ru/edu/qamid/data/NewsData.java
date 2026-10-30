package ru.edu.qamid.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class NewsData {
    public String title;
    public String description;
    public String category;
    public final LocalDate date;
    public final int hour = 0;
    public final int minute = 0;

    public NewsData(String label) {
        this(label, LocalDate.now());
    }

    public NewsData(String label, LocalDate date) {
        this.date = date;
        title = "AUTO_" + label + "_" + UUID.randomUUID().toString().substring(0, 12);
        description = "Описание тестовой новости " + title;
    }

    public String dateText() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String timeText() {
        return String.format(java.util.Locale.ROOT, "%02d:%02d", hour, minute);
    }
}
