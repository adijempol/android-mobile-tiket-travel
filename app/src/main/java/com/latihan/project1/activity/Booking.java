package com.latihan.project1.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.latihan.project1.R;
import com.latihan.project1.database.DatabaseHelper;
import com.latihan.project1.session.SessionManager;

import java.util.Calendar;
import java.util.HashMap;


public class Booking extends AppCompatActivity {
    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Spinner spinAsal, spinTujuan, spinAnggota;
    SessionManager session;
    String email;
    int id_book;
    public String sAsal, sTujuan, sTanggal, sAnggota;
    int jmlAnggota;
    int hargaPerAnggota;
    int hargaTotalAnggota, hargaTotal;
    private EditText etTanggal;
    private DatePickerDialog dpTanggal;
    Calendar newCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dbHelper = new DatabaseHelper(Booking.this);
        db = dbHelper.getReadableDatabase();

        final String[] asal = {"Jakarta", "Bandung", "Bali", "Yogyakarta", "Surabaya"};
        final String[] tujuan = {"Jakarta", "Bandung", "Bali", "Yogyakarta", "Surabaya"};
        final String[] anggota = {"0", "1", "2", "3", "4", "5"};

        spinAsal = findViewById(R.id.asal);
        spinTujuan = findViewById(R.id.tujuan);
        spinAnggota = findViewById(R.id.anggota);

        ArrayAdapter<CharSequence> adapterAsal = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, asal);
        adapterAsal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAsal.setAdapter(adapterAsal);

        ArrayAdapter<CharSequence> adapterTujuan = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, tujuan);
        adapterTujuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTujuan.setAdapter(adapterTujuan);

        ArrayAdapter<CharSequence> adapterAnggota = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, anggota);
        adapterAnggota.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAnggota.setAdapter(adapterAnggota);

        spinAsal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sAsal = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinTujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sTujuan = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinAnggota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sAnggota = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        spinAnak.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                sAnak = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        Button btnBook = findViewById(R.id.book);

        etTanggal = findViewById(R.id.tanggal_berangkat);
        etTanggal.setInputType(InputType.TYPE_NULL);
        etTanggal.requestFocus();
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);
        setDateTimeField();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perhitunganHarga();
                if (sAsal != null && sTujuan != null && sTanggal != null && sAnggota != null) {
                    if ((sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("jakarta"))
                            || (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("bandung"))
                            || (sAsal.equalsIgnoreCase("bali") && sTujuan.equalsIgnoreCase("bali"))
                            || (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("yogyakarta"))
                            || (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("surabaya"))) {
                        Toast.makeText(Booking.this, "Asal dan Tujuan tidak boleh sama !", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(Booking.this)
                                .setTitle("Ingin booking sekarang?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            db.execSQL("INSERT INTO TB_BOOK (asal, tujuan, tanggal, anggota) VALUES ('" +
                                                    sAsal + "','" +
                                                    sTujuan + "','" +
                                                    sTanggal + "','" +
                                                    sAnggota + "');");
                                            cursor = db.rawQuery("SELECT id_book FROM TB_BOOK ORDER BY id_book DESC", null);
                                            cursor.moveToLast();
                                            if (cursor.getCount() > 0) {
                                                cursor.moveToPosition(0);
                                                id_book = cursor.getInt(0);
                                            }
                                            db.execSQL("INSERT INTO TB_HARGA (username, id_book, harga, harga_total) VALUES ('" +
                                                    email + "','" +
                                                    id_book + "','" +
                                                    hargaTotalAnggota + "','" +
                                                    hargaTotal + "');");
                                            Toast.makeText(Booking.this, "Booking berhasil", Toast.LENGTH_LONG).show();
                                            finish();
                                        } catch (Exception e) {
                                            Toast.makeText(Booking.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Tidak", null)
                                .create();
                        dialog.show();
                    }
                } else {
                    Toast.makeText(Booking.this, "Mohon lengkapi data pemesanan!", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupToolbar();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbKrl);
        toolbar.setTitle("Form Booking");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void perhitunganHarga() {
        if (sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("surabaya")) {
            hargaPerAnggota = 100000;

        } else if (sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("bandung")) {
            hargaPerAnggota = 200000;

        } else if (sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("bali")) {
            hargaPerAnggota = 150000;
//            hargaAnak = 120000;
        } else if (sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("yogyakarta")) {
            hargaPerAnggota = 180000;
//            hargaAnak = 140000;
        } else if (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("jakarta")) {
            hargaPerAnggota = 100000;
//            hargaAnak = 70000;
        } else if (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("surabya")) {
            hargaPerAnggota = 120000;
//            hargaAnak = 100000;
        } else if (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("bali")) {
            hargaPerAnggota = 120000;
//            hargaAnak = 90000;
        } else if (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("yogyakarta")) {
            hargaPerAnggota = 190000;
//            hargaAnak = 160000;
        } else if (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("jakarta")) {
            hargaPerAnggota = 200000;
//            hargaAnak = 150000;
        } else if (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("bandung")) {
            hargaPerAnggota = 120000;
//            hargaAnak = 100000;
        } else if (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("bali")) {
            hargaPerAnggota = 170000;
//            hargaAnak = 130000;
        } else if (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("yogyakarta")) {
            hargaPerAnggota = 180000;
//            hargaAnak = 150000;
        } else if (sAsal.equalsIgnoreCase("bali") && sTujuan.equalsIgnoreCase("jakarta")) {
            hargaPerAnggota = 150000;
//            hargaAnak = 120000;
        } else if (sAsal.equalsIgnoreCase("bali") && sTujuan.equalsIgnoreCase("surabaya")) {
            hargaPerAnggota = 120000;
//            hargaAnak = 90000;
        } else if (sAsal.equalsIgnoreCase("bali") && sTujuan.equalsIgnoreCase("bandung")) {
            hargaPerAnggota = 80000;
//            hargaAnak = 40000;
        } else if (sAsal.equalsIgnoreCase("bali") && sTujuan.equalsIgnoreCase("yogyakarta")) {
            hargaPerAnggota = 170000;
//            hargaAnak = 130000;
        } else if (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("jakarta")) {
            hargaPerAnggota = 180000;
//            hargaAnak = 140000;
        } else if (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("bandung")) {
            hargaPerAnggota = 190000;
//            hargaAnak = 160000;
        } else if (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("surabaya")) {
            hargaPerAnggota = 80000;
//            hargaAnak = 40000;
        } else if (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("bali")) {
            hargaPerAnggota = 180000;
//            hargaAnak = 150000;
        }

        jmlAnggota = Integer.parseInt(sAnggota);
//        jmlAnak = Integer.parseInt(sAnak);

        hargaTotalAnggota = jmlAnggota * hargaPerAnggota;
//        hargaTotalAnak = jmlAnak * hargaAnak;
        hargaTotal = hargaTotalAnggota;
    }

    private void setDateTimeField() {
        etTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpTanggal.show();
            }
        });

        dpTanggal = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei",
                        "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                sTanggal = dayOfMonth + " " + bulan[monthOfYear] + " " + year;
                etTanggal.setText(sTanggal);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}