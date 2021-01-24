package com.example.check_in_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<String> locais = new ArrayList<>();
    private static List<String> categorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Searching places already visited*/
        Cursor c = BancoDadosSingleton.getInstance().buscar("checkin", new String[]{"local"},"","");
        while(c.moveToNext()){
            int l = c.getColumnIndex("local");
            locais.add(c.getString(l));
        }
        c.close();

        /*AutoComplete bar coding*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, locais);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(adapter);

        /*Spinner itens*/
        c = BancoDadosSingleton.getInstance().buscar("Categoria", new String[]{"nome"},"","");
        while(c.moveToNext()){
            int n = c.getColumnIndex("nome");
            categorias.add(c.getString(n));
        }
        c.close();

        /*Spinner*/
        Spinner spinner = (Spinner) findViewById(R.id.spinnerCategoria);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categorias);
        spinner.setAdapter(adapterSpinner);

        /*Maps Test*/
        Button testBt = findViewById(R.id.buttonTest);
        testBt.setOnClickListener(v -> {
            Intent it = new Intent(this, MapsActivity.class);
            startActivity(it);
        });
    }
}