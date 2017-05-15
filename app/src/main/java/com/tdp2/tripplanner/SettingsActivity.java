package com.tdp2.tripplanner;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.tdp2.tripplanner.helpers.LocaleHandler;


public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private int check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LocaleHandler.updateLocaleSettings(this);
        configToolbar();
        configSpinner();
    }

    private void configToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(this.getString(R.string.action_settings));
    }

    private void configSpinner() {
        TextView label = (TextView) findViewById(R.id.languaje_label);
        label.setText(R.string.Languaje);
        Spinner spinner = (Spinner) findViewById(R.id.languaje_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languaje_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        check = 0;
        String actual = LocaleHandler.codeToLanguage(LocaleHandler.loadLanguageSelection(this));
        spinner.setSelection(adapter.getPosition(actual));
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if (check == 0) {
            check++;
            return;
        }
        String seleccionado = (String) adapterView.getItemAtPosition(pos);
        LocaleHandler.saveLanguageSelection(this, seleccionado);
        Intent refresh = new Intent(this, SettingsActivity.class);
        startActivity(refresh);
        finish();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
