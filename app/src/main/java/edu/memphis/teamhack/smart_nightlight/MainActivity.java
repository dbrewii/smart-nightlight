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
import java.util.Set;
import java.util.ArrayList;
import java.util.UUID;

import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import edu.memphis.teamhack.smart_nightlight.BluetoothActivity;

import static android.support.v4.math.MathUtils.clamp;
import static edu.memphis.teamhack.smart_nightlight.R.id.textView;
//import android.widget.AdapterView.OnClickListener

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {
    Button button;
    Button btBtn;
    Button sendBtn;
    SeekBar intensitySB;
    SeekBar brightnessSB;


    ListView deviceList;
    private Set pairedDevices;
    //private int colorHex=0x345f21;

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

    int intensity;
    int brightness;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String[] presetsArray = getResources().getStringArray(R.array.presets_array);
        //Toast.makeText(getApplicationContext(),planetsArray[pos], Toast.LENGTH_LONG).show();
        // An item was selected. You can retrieve the selected item using
        if(presetsArray[pos].equals("Baby")){

            Toast.makeText(getApplicationContext(),"pap",Toast.LENGTH_LONG).show();
        }
        if(presetsArray[pos].equals("Pet")){
            Toast.makeText(getApplicationContext(),"pop",Toast.LENGTH_LONG).show();
        }
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.wtf("wtfTag","yo");
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            //Bluetooth
        Intent newint = getIntent();
        address = newint.getStringExtra(BluetoothActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device

        new ConnectBT().execute(); //Call the class to connect



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

        // Locate the button in activity_main.xml
        intensitySB = (SeekBar) findViewById(R.id.seekBar4);
        brightnessSB = (SeekBar) findViewById(R.id.seekBar5);
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
                sendParam("c00ff00");
            }
        });

        intensitySB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int intensity = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                intensity = progressValue;
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

        brightnessSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int brightness = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                brightness = progressValue;
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




    public void sendParam(String param){
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(param.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
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
    //send Bluetooth signal to microcontroller
    public void sendSignal(String colorHex){

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

    public int[] getRGBFromK(int temperature) {
        // Used this: https://gist.github.com/paulkaplan/5184275 at the beginning
        // based on http://stackoverflow.com/questions/7229895/display-temperature-as-a-color-with-c
        // this answer: http://stackoverflow.com/a/24856307
        // (so, just interpretation of pseudocode in Java)

        double x = temperature / 1000.0;
        if (x > 40) {
            x = 40;
        }
        double red;
        double green;
        double blue;

        // R
        if (temperature < 6527) {
            red = 1;
        } else {
            double[] redpoly = {4.93596077e0, -1.29917429e0,
                    1.64810386e-01, -1.16449912e-02,
                    4.86540872e-04, -1.19453511e-05,
                    1.59255189e-07, -8.89357601e-10};
            //red = poly(redpoly, x);

        }
        // G
        if (temperature < 850) {
            green = 0;
        } else if (temperature <= 6600) {
            double[] greenpoly = {-4.95931720e-01, 1.08442658e0,
                    -9.17444217e-01, 4.94501179e-01,
                    -1.48487675e-01, 2.49910386e-02,
                    -2.21528530e-03, 8.06118266e-05};
            //green = poly(greenpoly, x);
        } else {
            double[] greenpoly = {3.06119745e0, -6.76337896e-01,
                    8.28276286e-02, -5.72828699e-03,
                    2.35931130e-04, -5.73391101e-06,
                    7.58711054e-08, -4.21266737e-10};

            //green = poly(greenpoly, x);
        }
        // B
        if (temperature < 1900) {
            blue = 0;
        } else if (temperature < 6600) {
            double[] bluepoly = {4.93997706e-01, -8.59349314e-01,
                    5.45514949e-01, -1.81694167e-01,
                    4.16704799e-02, -6.01602324e-03,
                    4.80731598e-04, -1.61366693e-05};
            //blue = poly(bluepoly, x);
        } else {
            blue = 1;
        }

        //red = clamp(red, 0, 1);
        //blue = clamp(blue, 0, 1);
        //green = clamp(green, 0, 1);
        return null; // new Color((float) red, (float) green, (float) blue);
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
