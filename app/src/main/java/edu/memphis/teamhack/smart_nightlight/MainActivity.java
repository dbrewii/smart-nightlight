package edu.memphis.teamhack.smart_nightlight;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ListView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.ArrayList;
import java.util.UUID;

import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import edu.memphis.teamhack.smart_nightlight.BluetoothActivity;

import static android.os.Build.VERSION_CODES.M;
import static android.support.v4.math.MathUtils.clamp;
import static edu.memphis.teamhack.smart_nightlight.R.id.textView;
//import android.widget.AdapterView.OnClickListener

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {
    Button button;
    Button btBtn;
    Button sendBtn;
    SeekBar intensitySB;
    SeekBar brightnessSB;
    View colorView;


    ListView deviceList;
    private Set pairedDevices;

    //Bluetooth vars
    private BluetoothAdapter mBTAdapter;
    private TextView mBluetoothStatus;
    private ArrayAdapter<String> mBTArrayAdapter;

    Button btnOn, btnOff, btnDis;
    TextView lumn;
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //set a default value for each
    private final int DEFAULT_BRIGHT = 50;
    private final int DEFAULT_INTENSITY = 1000;
    private int intensity= DEFAULT_INTENSITY;
    private int brightness=DEFAULT_BRIGHT;
    private String colorHex=KtoRGB.k2hex(intensity);
    private final int MAX_TEMP = 5000;
    private final int MAX_BRIGHT = 100;
    private final int INTERVAL_TEMP = 100;
    private final int INTERVAL_BRIGHT = 1;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String[] presetsArray = getResources().getStringArray(R.array.presets_array);
        //Toast.makeText(getApplicationContext(),planetsArray[pos], Toast.LENGTH_LONG).show();
        // An item was selected. You can retrieve the selected item using
        if(presetsArray[pos].equals("Baby")){

            //Toast.makeText(getApplicationContext(),"pap",Toast.LENGTH_LONG).show();
        }
        if(presetsArray[pos].equals("Pet")){
            //Toast.makeText(getApplicationContext(),"pop",Toast.LENGTH_LONG).show();
        }
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.wtf("wtfTag","yo");
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            //Bluetooth
        //comment out when Arduino is absent
        //Intent newint = getIntent();
        //address = newint.getStringExtra(BluetoothActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //new ConnectBT().execute(); //Call the class to connect


        setColorHex();
        //other
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

        // Locate the button and all other GUI shtuff in activity_main.xml
        intensitySB = (SeekBar) findViewById(R.id.seekBar4);
        brightnessSB = (SeekBar) findViewById(R.id.seekBar5);
        colorView = (View) findViewById(R.id.rectangle_at_the_top);

        colorView.setBackgroundColor(Color.parseColor("#"+colorHex));
        intensitySB.setMax(MAX_TEMP);
        intensitySB.setProgress(intensity);
        intensitySB.incrementProgressBy(INTERVAL_TEMP);

        brightnessSB.setMax(MAX_BRIGHT);
        brightnessSB.setProgress(DEFAULT_BRIGHT);
        brightnessSB.incrementProgressBy(INTERVAL_BRIGHT);

        button = (Button) findViewById(R.id.button);
        sendBtn = (Button) findViewById(R.id.button4);
        btBtn = (Button)findViewById(R.id.button2);

        // Capture button clicks
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(myIntent);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.presets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //Bluetooth module

        btBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });

        sendBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                    //sendParam(colorHex);
            }
        });

        intensitySB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                intensity = progressValue;
                setColorHex();
                colorView.setBackgroundColor(Color.parseColor("#"+colorHex));
                //sending parameters to LED in realtime!
                sendParam(colorHex);
                //System.out.println("Kelvin:" + intensity);
                //System.out.println("Color hex:" + KtoRGB.k2hex(intensity));
                //Toast.makeText(getApplicationContext(), "Kelvin:" + intensity, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Color hex:" + KtoRGB.k2hex(intensity), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });

        brightnessSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                brightness = progressValue;
                setColorHex();
                colorView.setBackgroundColor(Color.parseColor("#"+colorHex));
                sendParam(colorHex);
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });

    }



    //sending the colorHex value to the Arduino
    public void sendParam(String param){
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(("c"+param).getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }
    //sets colorHex to be sent to light based upon intensity and brightness
    public void setColorHex(){
        double [] rgb;
        rgb=KtoRGB.k2rgb(intensity);

        //brightness scales the color, so if you have R=255,G=0,B=0;
        //brightness = 50 means that R=128. It's considered the same H & S in an HSV colorspace
        rgb[0]=((double)brightness/100.0)*rgb[0];
        rgb[1]=((double)brightness/100.0)*rgb[1];
        rgb[2]=((double)brightness/100.0)*rgb[2];
        this.colorHex=KtoRGB.rgb2hex(rgb);
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

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            //progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            //progress.dismiss();
        }
    }

}
