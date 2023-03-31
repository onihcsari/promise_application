package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class month_display extends AppCompatActivity {
    private MaterialCalendarView calendarView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        calendarView = findViewById(R.id.calendarView);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.menu_week){
            calendarView.state().edit()
                    .setCalendarDisplayMode(CalendarMode.WEEKS)
                    .commit();
            return true;
        } else if(id==R.id.menu_month){
            calendarView.state().edit()
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
