package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alifia.mymaxxioadmin.model.Event;
import com.alifia.mymaxxioadmin.model.EventItem;
import com.alifia.mymaxxioadmin.model.ListItem;
import com.alifia.mymaxxioadmin.model.TitleItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class Kegiatan extends AppCompatActivity {

    private RecyclerView eventList;
    private EventAdapter adapter;
    private Spinner btnTingkatanEvent;
    private ImageButton btnAddEvent;
    private MaterialToolbar btnKembali;
    private EventDalamReviewAdapter adapterDalamReview;
    private EventDitolakAdapter adapterDitolak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kegiatan);

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // untuk menampilkan list kegiatan, digunakan recyclerview
        eventList = findViewById(R.id.eventList);
        eventList.setLayoutManager(new LinearLayoutManager(this));

        btnAddEvent = findViewById(R.id.button_add_event);
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddEvent.class);
                startActivity(intent);
            }
        });

        btnTingkatanEvent = findViewById(R.id.dropdown_event);
        ArrayAdapter<CharSequence> adapterTingkatanEvent = ArrayAdapter.createFromResource(
                this,
                R.array.event_array,
                R.layout.custom_spinner_item
        );
        adapterTingkatanEvent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btnTingkatanEvent.setAdapter(adapterTingkatanEvent);
        btnTingkatanEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTingkatan = btnTingkatanEvent.getSelectedItem().toString();

                if (selectedTingkatan.equals("Kegiatan Usulan (Dalam Review)")) {
                    setupFirestoreDalamReview();
                } else if (selectedTingkatan.equals("Kegiatan Usulan (Ditolak)")){
                    setupFirestoreDitolak();
                } else { // selain dalam review & ditolak (semua, nasional, reg, chap)
                    setupFirestore();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setupFirestore();

    }

    private void setupFirestoreDitolak() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore store = FirebaseFirestore.getInstance();

        Query snapshot = store.collection("kegiatan")
                .whereEqualTo("isApproved", 2)
                .orderBy("tgl", Query.Direction.DESCENDING);

        Log.d("TAG", btnTingkatanEvent.getSelectedItem().toString());

        // dari admin, udh bener
        snapshot.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(
                    @Nullable QuerySnapshot snapshot,
                    @Nullable FirebaseFirestoreException error
            ) {
                if (error != null) {
                    adapterDitolak = new EventDitolakAdapter(new ArrayList<>());
                    eventList.setAdapter(adapterDitolak);
                    return;
                }

                if (snapshot != null) {
                    List<com.alifia.mymaxxioadmin.model.Event> events = snapshot.toObjects(com.alifia.mymaxxioadmin.model.Event.class);
                    LinkedHashMap<String, List<com.alifia.mymaxxioadmin.model.Event>> groups = groupEvents(events);
                    List<ListItem> consolidatedList = new ArrayList<>();
                    for (String date : groups.keySet()) {
                        TitleItem titleItem = new TitleItem();
                        titleItem.setDate(date);
                        consolidatedList.add(titleItem);

                        for (com.alifia.mymaxxioadmin.model.Event event : groups.get(date)) {
                            EventItem eventItem = new EventItem();
                            eventItem.setEvent(event);
                            consolidatedList.add(eventItem);
                        }
                    }
                    adapterDitolak = new EventDitolakAdapter(consolidatedList);
                    eventList.setAdapter(adapterDitolak);
                } else {
                    adapterDitolak = new EventDitolakAdapter(new ArrayList<>());
                    eventList.setAdapter(adapterDitolak);
                }
            }
        });
    }

    private void setupFirestoreDalamReview() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore store = FirebaseFirestore.getInstance();

        Query snapshot = store.collection("kegiatan")
                .whereEqualTo("isApproved", 0)
                .orderBy("tgl", Query.Direction.DESCENDING);

        Log.d("TAG", btnTingkatanEvent.getSelectedItem().toString());

        snapshot.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(
                    @Nullable QuerySnapshot snapshot,
                    @Nullable FirebaseFirestoreException error
            ) {
                if (error != null) {
                    adapterDalamReview = new EventDalamReviewAdapter(new ArrayList<>());
                    eventList.setAdapter(adapterDalamReview);
                    return;
                }

                if (snapshot != null) {
                    List<com.alifia.mymaxxioadmin.model.Event> events = snapshot.toObjects(com.alifia.mymaxxioadmin.model.Event.class);
                    LinkedHashMap<String, List<com.alifia.mymaxxioadmin.model.Event>> groups = groupEvents(events);
                    List<ListItem> consolidatedList = new ArrayList<>();
                    for (String date : groups.keySet()) {
                        TitleItem titleItem = new TitleItem();
                        titleItem.setDate(date);
                        consolidatedList.add(titleItem);

                        for (com.alifia.mymaxxioadmin.model.Event event : groups.get(date)) {
                            EventItem eventItem = new EventItem();
                            eventItem.setEvent(event);
                            consolidatedList.add(eventItem);
                        }
                    }
                    adapterDalamReview = new EventDalamReviewAdapter(consolidatedList);
                    eventList.setAdapter(adapterDalamReview);
                } else {
                    adapterDalamReview = new EventDalamReviewAdapter(new ArrayList<>());
                    eventList.setAdapter(adapterDalamReview);
                }
            }
        });
    }

    public void setupFirestore() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore store = FirebaseFirestore.getInstance();

        // "Semua Kegiatan"
        Query snapshot = store.collection("kegiatan")
                .whereEqualTo("isApproved", 1)
                .orderBy("tgl", Query.Direction.DESCENDING);

        switch (btnTingkatanEvent.getSelectedItem().toString()) {
            case "Nasional":
                snapshot = store.collection("kegiatan")
                        .whereEqualTo("namaTingkatan", "Nasional")
                        .whereEqualTo("isApproved", 1)
                        .orderBy("tgl", Query.Direction.DESCENDING);
                break;
            case "Regional":
                snapshot = store.collection("kegiatan")
                        .whereEqualTo("namaTingkatan", "Regional")
                        .whereEqualTo("isApproved", 1)
                        .orderBy("tgl", Query.Direction.DESCENDING);
                break;
            case "Chapter":
                snapshot = store.collection("kegiatan")
                        .whereEqualTo("namaTingkatan", "Chapter")
                        .whereEqualTo("isApproved", 1)
                        .orderBy("tgl", Query.Direction.DESCENDING);
                break;
        }

        Log.d("TAG", btnTingkatanEvent.getSelectedItem().toString());

        snapshot.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(
                    @Nullable QuerySnapshot snapshot,
                    @Nullable FirebaseFirestoreException error
            ) {
                if (error != null) {
                    adapter = new EventAdapter(new ArrayList<>());
                    eventList.setAdapter(adapter);
                    return;
                }

                if (snapshot != null) {
                    List<com.alifia.mymaxxioadmin.model.Event> events = snapshot.toObjects(com.alifia.mymaxxioadmin.model.Event.class);
                    LinkedHashMap<String, List<com.alifia.mymaxxioadmin.model.Event>> groups = groupEvents(events);
                    List<ListItem> consolidatedList = new ArrayList<>();
                    for (String date : groups.keySet()) {
                        TitleItem titleItem = new TitleItem();
                        titleItem.setDate(date);
                        consolidatedList.add(titleItem);

                        for (com.alifia.mymaxxioadmin.model.Event event : groups.get(date)) {
                            EventItem eventItem = new EventItem();
                            eventItem.setEvent(event);
                            consolidatedList.add(eventItem);
                        }
                    }
                    adapter = new EventAdapter(consolidatedList);
                    eventList.setAdapter(adapter);
                } else {
                    adapter = new EventAdapter(new ArrayList<>());
                    eventList.setAdapter(adapter);
                }
            }
        });
    }

    public LinkedHashMap<String, List<Event>> groupEvents(List<Event> events) {
        LinkedHashMap<String, List<Event>> groupedHashMap = new LinkedHashMap<>();

        for (Event event : events) {
            Timestamp timestamp = event.getTgl();
            if (timestamp != null) {
                Date date = timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("ID"));
                String monthYear = sdf.format(date);

                if (groupedHashMap.containsKey(monthYear)) {
                    groupedHashMap.get(monthYear).add(event);
                } else {
                    List<Event> list = new ArrayList<>();
                    list.add(event);
                    groupedHashMap.put(monthYear, list);
                }
            } else {
                Log.w("EventActivity", "Timestamp null untuk event: " + event.getNamaKegiatan());
            }
        }

        return groupedHashMap;
    }
}