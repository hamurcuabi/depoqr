package com.emrehmrc.depoqr;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TakvimGorevler extends AppCompatActivity {

    ActionBar ab;
    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss",
            Locale.getDefault());
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM- yyyy",
            Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takvim_gorevler);
        ab = getSupportActionBar();
        Date dateNow = new Date();
        //ab.setTitle(simpleDateFormat.format(dateNow.getDate()));
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        Date date = null;
        String dateInString = "22-04-2018 10:20:56";
        try {
             date = dateFormatMonth.parse(dateInString);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Event ev1 = new Event(Color.RED, date.getTime(), "EVENTSSS");
        compactCalendar.addEvent(ev1);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();

                if (dateClicked.toString().compareTo("Tue Apr 17 00:00:00 GMT+03:00 2018") == 0) {
                    Toast.makeText(context, "Teachers' Professional Day", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "NO EVENTSSS", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(simpleDateFormat.format(firstDayOfNewMonth));
            }
        });
    }
}
