package cs240.familymapclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Objects;

import cs240.familymapclient.DataCache;
import cs240.familymapclient.R;
import cs240.familymapclient.Settings;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(this.getSupportActionBar()).setTitle(R.string.settings);

        DataCache dataCache = DataCache.getInstance();
        Settings settings = dataCache.getSettings();

        //create all switch onClickListeners, which set their respective setting.

        SwitchCompat lifeStoryLineSwitch = findViewById(R.id.life_story_lines_switch);
        lifeStoryLineSwitch.setChecked(settings.isLifeStoryLinesOn());
        lifeStoryLineSwitch.setOnClickListener(view -> settings.setLifeStoryLines(lifeStoryLineSwitch.isChecked()));

        SwitchCompat familyTreeLinesSwitch = findViewById(R.id.family_tree_lines_switch);
        familyTreeLinesSwitch.setChecked(settings.isFamilyTreeLinesOn());
        familyTreeLinesSwitch.setOnClickListener(view -> settings.setFamilyTreeLines(familyTreeLinesSwitch.isChecked()));

        SwitchCompat spouseLinesSwitch = findViewById(R.id.spouse_lines_switch);
        spouseLinesSwitch.setChecked(settings.isSpouseLinesOn());
        spouseLinesSwitch.setOnClickListener(view -> settings.setSpouseLines(spouseLinesSwitch.isChecked()));

        SwitchCompat paternalEventsSwitch = findViewById(R.id.paternal_events_switch);
        paternalEventsSwitch.setChecked(settings.isPaternalSideFilterOn());
        paternalEventsSwitch.setOnClickListener(view -> settings.setPaternalSideFilter(paternalEventsSwitch.isChecked()));

        SwitchCompat maternalEventsSwitch = findViewById(R.id.maternal_events_switch);
        maternalEventsSwitch.setChecked(settings.isMaternalSideFilterOn());
        maternalEventsSwitch.setOnClickListener(view -> settings.setMaternalSideFilter(maternalEventsSwitch.isChecked()));

        SwitchCompat maleEventsSwitch = findViewById(R.id.male_events_switch);
        maleEventsSwitch.setChecked(settings.isMaleFilterOn());
        maleEventsSwitch.setOnClickListener(view -> settings.setMaleFilter(maleEventsSwitch.isChecked()));

        SwitchCompat femaleEventsSwitch = findViewById(R.id.female_events_switch);
        femaleEventsSwitch.setChecked(settings.isFemaleFilterOn());
        femaleEventsSwitch.setOnClickListener(view -> settings.setFemaleFilter(femaleEventsSwitch.isChecked()));

        //logout button.
        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> logout());
    }

    private void logout() {
        DataCache.getInstance().logout();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }


}