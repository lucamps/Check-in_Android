package com.example.check_in_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.view.Menu;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private static final int LOCATION_PERMISSION = 2;
    public int TEMPO_REQUISICAO_LATLONG = 5000;
    public int DISTANCIA_MIN_METROS = 0;
    public Location currentLocation;
    public String provider;
    public LocationManager lm;
    public Criteria criteria;
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
    }

    /*Menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.itemMapa:
                Intent it = new Intent(this, MapsActivity.class);
                startActivity(it);
                Log.i("main_menu","mapa");
                return true;
            case R.id.itemGestao:
                Log.i("main_menu","gestão");
                return true;
            case R.id.itemLugares:
                Log.i("main_menu","lugares");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("MissingPermission")
    public void iniciaGeolocation(Context ctx) {
        //obtem o melhor provedor habilitado com o critério
        provider = lm.getBestProvider(criteria, true);

        if (provider == null) {
            Log.e("PROVEDOR", "Nenhum provedor encontrado");
        } else {
            Log.i("PROVEDOR", "Esta sendo utilizado o provedor " + provider);

            lm.requestLocationUpdates(provider, TEMPO_REQUISICAO_LATLONG, DISTANCIA_MIN_METROS, (LocationListener) ctx);
        }
    }
    public void requestLocationPermission(){
        //verifica se precisa explicar a permissão
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {

            //pede permissão
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , LOCATION_PERMISSION
            );

            Toast.makeText(this
                    , "Permita o acesso a localização do dispositivo para\n" +
                            "medir a distância até o local selecionado."
                    , Toast.LENGTH_LONG).show();

        } else {
            //pede permissão
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , LOCATION_PERMISSION
            );
        }
    }
    public void configuraCriterioLocation() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        PackageManager packageManager = getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if (hasGPS) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            Log.i("LOCATION", "usando GPS");
        } else {
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            Log.i("LOCATION", "usando WI-FI ou dados");
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            configuraCriterioLocation();
            iniciaGeolocation(this);
        }
    }

    /*Updating latLng data*/

    @Override
    public void onLocationChanged(Location location) {
        TextView latitudeText = findViewById(R.id.textViewLatitude);
        TextView longitudeText = findViewById(R.id.textViewLongitude);
        currentLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latitudeText.setText("Latitude:        " + latLng.latitude);
        longitudeText.setText("Longitude:     " + latLng.longitude);
    }
}