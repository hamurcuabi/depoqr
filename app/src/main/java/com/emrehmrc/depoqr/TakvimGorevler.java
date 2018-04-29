package com.emrehmrc.depoqr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TakvimGorevler extends AppCompatActivity {

    ActionBar ab;
    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss",
            Locale.getDefault());
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM - yyyy",
            Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takvim_gorevler);
        ab = getSupportActionBar();

         ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);
        Date dateNow = new Date();
        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setUseThreeLetterAbbreviation(true);


        allEvents();


        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();

                if (dateClicked.toString().compareTo("Tue Apr 17 00:00:00 GMT+03:00 2018") == 0) {
                    Toast.makeText(context, "Teachers' Professional Day", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "NO EVENTSSS", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(simpleDateFormat.format(firstDayOfNewMonth));
            }
        });
    }

    private void allEvents() {

        Date date = null;
        Date date2 = null;
        Date date3 = null;
        Date date4 = null;

        String dateInString = "22-04-2018 10:20:56";
        String dateInString2 = "23-04-2018 10:20:56";
        String dateInString3 = "24-04-2018 10:20:56";
        String dateInString4 = "25-04-2018 10:20:56";
        try {
            date = dateFormatMonth.parse(dateInString);
            date2 = dateFormatMonth.parse(dateInString2);
            date3 = dateFormatMonth.parse(dateInString3);
            date4 = dateFormatMonth.parse(dateInString4);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //  compactCalendar.addEvent(ev1);
        ArrayList<Event> events = new ArrayList<>();

        Event ev1 = new Event(Color.RED, date.getTime(), "EVENTSSS");
        events.add(ev1);
        Event ev2 = new Event(Color.RED, date2.getTime(), "EVENTSSS");
        events.add(ev2);
        Event ev3 = new Event(Color.RED, date3.getTime(), "EVENTSSS");
        events.add(ev3);
        Event ev4 = new Event(Color.RED, date4.getTime(), "EVENTSSS");
        events.add(ev4);

        for(int i=0;i<events.size();i++){

            compactCalendar.addEvent(events.get(i));

        }


    }
}
