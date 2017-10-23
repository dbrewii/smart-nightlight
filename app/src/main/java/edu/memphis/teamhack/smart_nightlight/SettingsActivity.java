package edu.memphis.teamhack.smart_nightlight;

import android.app.Activity;
import android.content.ComponentName;
import android.os.Bundle;

import com.rarepebble.colorpicker.ColorPickerView;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ComponentName cn = getComponentName();

        ColorPickerView picker = (ColorPickerView)findViewById(R.id.colorPicker);
        picker.setColor(0xffff0000);
    }
}
