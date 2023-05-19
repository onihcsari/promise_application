package com.example.project1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class month_display extends AppCompatActivity {

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private AlarmManager alarmManager;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> Arrayevents = new ArrayList<>();
    public FirebaseAuth mAuth;
    public EditText editTextUID;
    private MaterialCalendarView calendarView;

    List<DBfile> events = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        calendarView = findViewById(R.id.calendarView);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        for(DBfile dbfile : events){
            String event = dbfile.getTime() + " " + dbfile.getLocation();
            Arrayevents.add(event);
        }

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrayevents);
        listView.setAdapter(adapter);

        editTextUID = findViewById(R.id.editTextUID);
        editTextUID.setText("UID: " + uid);

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (!notificationManager.areNotificationsEnabled()) {
            Toast.makeText(month_display.this, "알림 설정을 허용해야 합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, "Your channel ID");
            startActivity(intent);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id==R.id.menu_plus){
                    Intent intent = new Intent(month_display.this, SubActivity.class);
                    startActivity(intent);
                }
                    return true;
                // 아이템 선택에 대한 동작 구현
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 약속의 데이터를 가져옴
                DBfile selectedDBfile = events.get(position);

                // 약속 상세화면으로 이동하는 Intent 생성
                Intent intent = new Intent(month_display.this, detailActivity.class);

                // 약속 데이터를 Intent에 추가
                intent.putExtra("DBfile", selectedDBfile);

                // 약속 상세화면으로 이동
                startActivity(intent);
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Get the selected date
                Date date1 = date.getDate();
                LocalDate localDate = date.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateString = sdf.format(date1);

                fetchEventsFromFirebase(dateString);
                // Get events on the selected date

                // List<DBfile> eventsOnDate = getEventsOnDate(dateString);
                showEvents(events);

                // Highlight the selected date
                calendarView.setDateSelected(date, true);
            }
        });
    }

    private void showEvents(List<DBfile> events) {
        if (events.size() > 0) {
            List<String> eventStrings = new ArrayList<>();
            int count = 0;
            for (DBfile event : events) {
                count++;
                String eventString = "약속 " + count + "\n"
                        + "시간: " + event.getTime() + " "
                        + "장소: " + event.getLocation();
                eventStrings.add(eventString);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventStrings);
            listView.setAdapter(adapter);
        } else {
            // 약속이 없을 경우 빈 리스트를 보여줍니다.
            List<String> emptyList = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyList);
            listView.setAdapter(adapter);
        }
    }

    private void fetchEventsFromFirebase(String dateString) {
        databaseReference.child("DB").child(mAuth.getCurrentUser().getUid()).orderByChild("date").equalTo(dateString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list of events
                events.clear();

                // Iterate through each child node
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    // Convert the snapshot to a DBfile object
                    DBfile event = eventSnapshot.getValue(DBfile.class);

                    // Add the event to the list
                    events.add(event);
                }

                // Show the events on the screen
                showEvents(events);

                // Schedule notifications for the fetched events
                // scheduleNotification(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private List<DBfile> parseData(DataSnapshot dataSnapshot) {
        List<DBfile> events = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            DBfile event = new DBfile();

            String date = snapshot.child("date").getValue(String.class);
            String time = snapshot.child("time").getValue(String.class);
            String title = snapshot.child("title").getValue(String.class);
            String location = snapshot.child("location").getValue(String.class);
            String category = snapshot.child("category").getValue(String.class);
            String number = snapshot.child("number").getValue(String.class);

            event.setDate(date);
            event.setTime(time);
            event.setTitle(title);
            event.setLocation(location);
            event.setCategory(category);
            event.setNumber(number);

            events.add(event);
        }

        return events;
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
