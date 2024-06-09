package com.example.schedully;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ExamDateDecorator implements DayViewDecorator {

    private Set<CalendarDay> examDates;
    private Drawable highlightDrawable;

    public ExamDateDecorator(Context context, Set<CalendarDay> examDates) {
        this.examDates = examDates;
        this.highlightDrawable = ContextCompat.getDrawable(context, R.drawable.exam_date_highlight); // Ensure this drawable exists
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return examDates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(highlightDrawable);
    }
}
