package cs240.familymapclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import cs240.familymapclient.fragments.MapFragment;
import cs240.familymapclient.R;

public class EventActivity extends AppCompatActivity {

    public static final String EVENT_KEY = "EventKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container_event);

        if (fragment == null) {
            MapFragment mapFragment = new MapFragment();
            String eventID = getIntent().getStringExtra(EVENT_KEY);
            mapFragment.setSelectedEvent(eventID);
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container_event, mapFragment)
                    .commit();
        }
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