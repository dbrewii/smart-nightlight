package edu.memphis.teamhack.smart_nightlight;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ListView;
import java.util.Set;
import java.util.ArrayList;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//import android.widget.AdapterView.OnClickListener

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {
    Button button;
    Button btBtn;
    ListView deviceList;
    private BluetoothAdapter myBluetooth = null;
    private Set pairedDevices;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String[] planetsArray = getResources().getStringArray(R.array.planets_array);
        //Toast.makeText(getApplicationContext(),planetsArray[pos], Toast.LENGTH_LONG).show();
        // An item was selected. You can retrieve the selected item using
        if(planetsArray[pos].equals("Mercury")){
            Toast.makeText(getApplicationContext(),"pap",Toast.LENGTH_LONG).show();
        }
        if(planetsArray[pos].equals("Venus")){
            Toast.makeText(getApplicationContext(),"pop",Toast.LENGTH_LONG).show();
        }
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.wtf("wtfTag","yo");
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        // Locate the button in activity_main.xml
        button = (Button) findViewById(R.id.button);

        // Capture button clicks
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        SettingsActivity.class);
                startActivity(myIntent);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //Bluetooth module
        btBtn = (Button)findViewById(R.id.button2);
        btBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
