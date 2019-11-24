package com.example.mattilsynet.Innstillinger;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mattilsynet.R;

public class Innstillinger extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.innstillinger);
        //Setter inn fragment av innstillinger
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_layout, new SettingsFragment())
                .commit();

        //Setter tittel på toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Innstillinger");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //Sender bruker tilbake til hjem ved trykk på tilbakeknapp
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Klasse for innstillinger fragment
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.innstillinger_fragment, rootKey);

            //https://stackoverflow.com/questions/4005029/how-can-i-set-the-android-preference-summary-text-color/4047054#4047054
            Preference app_version = findPreference("app_version");
            //Viser app versjon hentet fra gradle
            Spannable summary = new SpannableString(getString(R.string.versionName));
            summary.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textWhiteSecondary)), 0, summary.length(), 0);
            app_version.setSummary(summary);
        }
    }
}