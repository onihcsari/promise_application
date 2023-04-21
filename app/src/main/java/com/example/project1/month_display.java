package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;

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
    public FirebaseAuth mAuth;
    private List<DBfile> allEvents = new ArrayList<>();
    private MaterialCalendarView calendarView;
    private LocalDate selectedDate;

    List<DBfile> events = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        calendarView = findViewById(R.id.calendarView);

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        // fetchEventsFromFirebase();

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

    public List<DBfile> getEventsOnDate(String localDate) {
        List<DBfile> events = new ArrayList<>();
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(uid);
        // Realtime Database에서 선택한 날짜에 해당하는 약속 정보 가져오기
        ref.orderByChild("date").equalTo(localDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String eventKey = snapshot.getKey();
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    String category = snapshot.child("category").getValue(String.class);
                    String number = snapshot.child("number").getValue(String.class);
                    DBfile dbfile = new DBfile(date, time, title, location, category, number);
                    events.add(dbfile);
                }

                // 가져온 약속 정보를 이용해서 달력 아래쪽에 약속 정보 보여주는 메소드 호출
                showEvents(events);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }

        });

        return events;
    }
    private void showEvents(List<DBfile> events) {
        TextView textView = findViewById(R.id.textView);

        if (events.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (DBfile event : events) {
                stringBuilder.append("Title: " + event.getTitle() + "\n");
                stringBuilder.append("Time: " + event.getTime() + "\n");
                stringBuilder.append("Location: " + event.getLocation() + "\n");
                stringBuilder.append("Category: " + event.getCategory() + "\n");
                stringBuilder.append("Number: " + event.getNumber() + "\n");
            }

            textView.setText(stringBuilder.toString());
        } else {
            textView.setText("No events on this day.");
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

                // Save the events to the allEvents list
                allEvents.clear();
                allEvents.addAll(events);
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
        return true;
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
