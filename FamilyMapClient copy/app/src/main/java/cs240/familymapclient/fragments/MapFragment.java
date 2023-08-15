package cs240.familymapclient.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.List;

import cs240.familymapclient.DataCache;
import cs240.familymapclient.R;
import cs240.familymapclient.Settings;
import cs240.familymapclient.activities.MainActivity;
import cs240.familymapclient.activities.PersonActivity;
import cs240.familymapclient.activities.SearchActivity;
import cs240.familymapclient.activities.SettingsActivity;
import model.Event;
import model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private Event selectedEvent;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Iconify.with(new FontAwesomeModule());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getActivity() instanceof MainActivity) {
            //the embedded mapFragment in main activity has a toolbar with menu items
            setHasOptionsMenu(true);
            androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            assert activity != null;
            activity.setSupportActionBar(toolbar);
        } else {
            //the embedded mapFragment in event activity does not, so I disable it here
            AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout_map);
            appBarLayout.setEnabled(false);
        }

        LinearLayout eventInfo = view.findViewById(R.id.map_event_info_view);
        eventInfo.setOnClickListener(view1 -> {
            if (selectedEvent != null) {
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                intent.putExtra(PersonActivity.PERSON_KEY, selectedEvent.getPersonID());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search_menu_item);
        searchMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                .color(Color.WHITE)
                .actionBarSize());

        MenuItem settingsMenuItem = menu.findItem(R.id.settings_menu_item);
        settingsMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                .color(Color.WHITE)
                .actionBarSize());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.search_menu_item:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings_menu_item:
                Intent intent1 = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        map.getUiSettings().setMapToolbarEnabled(false);

        DataCache dataCache = DataCache.getInstance();
        dataCache.updateDisplayed();

        createAllMarkers();

        if (selectedEvent != null) {
            //this centers the camera on the selectedEvent on startup, which is set when
            // EventActivity is started
            LatLng latLng = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            setEventInfoDisplay(selectedEvent);
        }

        map.setOnMarkerClickListener(marker -> {
            for (Polyline line : DataCache.getInstance().getPolylines()) {
                line.remove();
            }
            Event e = (Event) marker.getTag();
            setEventInfoDisplay(e);
            return false;
        });

        map.setOnMapClickListener(latLng -> {
            for (Polyline line : dataCache.getPolylines()) {
                line.remove();
            }
            setEventInfoDisplay(null);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //if the settings have changed, redo the map
        if (DataCache.getInstance().getSettings().changedFlag()) {
            DataCache.getInstance().updateDisplayed();
            map.clear();
            selectedEvent = null;
            setEventInfoDisplay(null);
            createAllMarkers();
            DataCache.getInstance().getSettings().resetChangedFlag();
        }
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }

    //called in EventActivity to pre-set the event
    public void setSelectedEvent(String eventID) {
        selectedEvent = DataCache.getInstance().getEventByID(eventID);
    }

    /*<---------------------------------------Marker creation functions--------------------------------------->*/
    private void createAllMarkers() {
        DataCache dataCache = DataCache.getInstance();
        Settings settings = dataCache.getSettings();

        for (Person p : dataCache.getPeopleCurrentlyDisplayed()) {
            for (Event e : dataCache.getPersonEvents(p.getPersonID())) {
                createMarker(e);
            }
        }

        //Will center camera on user's earliest event if one isn't already selected
        if (selectedEvent == null) {
            Event earliestEvent = dataCache.getEarliestEventForPerson(dataCache.getCurrentUserID());
            LatLng eventLocation = new LatLng(earliestEvent.getLatitude(), earliestEvent.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
        }

    }

    //create a marker using the assigned color. set tag to the event. Add to dataCache.eventsOnDisplay.
    private void createMarker(Event e) {
        DataCache dataCache = DataCache.getInstance();
        LatLng eventLocation = new LatLng(e.getLatitude(), e.getLongitude());
        float color = dataCache.getEventColors().get(e.getEventType().toLowerCase());
        Marker newMarker = map.addMarker(new MarkerOptions()
                .position(eventLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
        assert newMarker != null;
        newMarker.setTag(e);
    }


    /*<---------------------------------------Polyline creation functions--------------------------------------->*/
    private void drawAllPolylines(Event e) {
        DataCache dataCache = DataCache.getInstance();
        Person p = dataCache.getPersonByID(e.getPersonID());
        float width = 15.0f; //default line width, will shorten in family tree lines
        if (dataCache.getSettings().isLifeStoryLinesOn()) {
            drawLifeStoryLines(p, width);
        }
        if (dataCache.getSettings().isSpouseLinesOn()) {
            drawSpouseLines(e, p, width);
        }
        if (dataCache.getSettings().isFamilyTreeLinesOn()) {
            drawFamilyTreeLines(e, p, width);
        }
    }

    private void drawLifeStoryLines(Person person, float width) {
        List<Event> events = DataCache.getInstance().getPersonEvents(person.getPersonID());
        if (events.get(0) != null) {
            Event startEvent;
            Event endEvent;
            for (int i = 0; i < events.size() - 1; ++i) {
                startEvent = events.get(i);
                endEvent = events.get(i + 1);
                drawLine(startEvent, endEvent, Color.BLUE, width);
            }
        }
    }

    private void drawSpouseLines(Event startEvent, Person startPerson, float width) {
        if (startPerson.getSpouseID() != null) {
            Event spouseEvent = DataCache.getInstance().getEarliestEventForPerson(startPerson.getSpouseID());
            if (spouseEvent != null) {
                if (DataCache.getInstance().isEventOnDisplay(spouseEvent)) {
                    drawLine(startEvent, spouseEvent, Color.RED, width);
                }
            }

        }
    }

    private void drawFamilyTreeLines(Event startEvent, Person startPerson, float width) {
        if (width <= 0) {
            return;
        }

        //recursively draw lines back for mothers, shortening the line by 5 each time
        if (startPerson.getMotherID() != null) {
            Person endPerson = DataCache.getInstance().getPersonByID(startPerson.getMotherID());
            Event endEvent = DataCache.getInstance().getEarliestEventForPerson(endPerson.getPersonID());
            if (endEvent != null) {
                if (DataCache.getInstance().isEventOnDisplay(endEvent)) {
                    drawLine(startEvent, endEvent, Color.GREEN, width);
                    drawFamilyTreeLines(endEvent, endPerson, width - 5);
                }
            }
        }

        //recursively draw lines back for fathers, shortening the line by 5 each time
        if (startPerson.getFatherID() != null) {
            Person endPerson = DataCache.getInstance().getPersonByID(startPerson.getFatherID());
            Event endEvent = DataCache.getInstance().getEarliestEventForPerson(endPerson.getPersonID());
            if (endEvent != null) {
                if (DataCache.getInstance().isEventOnDisplay(endEvent)) {
                    drawLine(startEvent, endEvent, Color.GREEN, width);
                    drawFamilyTreeLines(endEvent, endPerson, width - 5);
                }
            }
        }
    }

    //actually draw the dang thing
    private void drawLine(Event e1, Event e2, int color, float width) {
        LatLng startPoint = new LatLng(e1.getLatitude(), e1.getLongitude());
        LatLng endPoint = new LatLng(e2.getLatitude(), e2.getLongitude());

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(color)
                .width(width);

        Polyline line = map.addPolyline(options);
        DataCache.getInstance().addPolyline(line);
    }

    /*<---------------------------------------EventInfoDisplay functions--------------------------------------->*/
    private void setEventInfoDisplay(Event e) {
        if (e != null) {
            selectedEvent = e;
            displayEventInfo(e);
            drawAllPolylines(e);
        } else {
            selectedEvent = null;
            displayEventInfo(null);
        }
    }

    private void displayEventInfo(Event e) {
        TextView selectedEventPersonName = requireView().findViewById(R.id.selected_event_person_name);
        TextView selectedEventType = requireView().findViewById(R.id.selected_event_type);
        TextView selectedEventLocation = requireView().findViewById(R.id.selected_event_location);
        TextView selectedEventYear = requireView().findViewById(R.id.selected_event_year);
        ImageView icon = requireView().findViewById(R.id.gender_icon);

        if (e != null) {
            Person person = DataCache.getInstance().getPersonByID(e.getPersonID());

            if (person.getGender().equals("m")) {
                Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                        .color(Color.DKGRAY)
                        .sizeDp(60);
                icon.setImageDrawable(genderIcon);
            }
            else {
                Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                        .color(Color.DKGRAY)
                        .sizeDp(60);
                icon.setImageDrawable(genderIcon);
            }

            //set text for name textview
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String name = firstName + " " + lastName;
            selectedEventPersonName.setText(name);

            //set text for event type textview
            selectedEventType.setText(e.getEventType());

            //set text for location textview
            String country = e.getCountry();
            String city = e.getCity();
            String location = city + ", " + country;
            selectedEventLocation.setText(location);

            //set text for year textview
            selectedEventYear.setText(String.valueOf(e.getYear()));
        } else {
            //no event on display
            selectedEventPersonName.setText("");
            selectedEventType.setText("");
            selectedEventLocation.setText("");
            selectedEventYear.setText("");
            icon.setImageResource(android.R.color.transparent);
        }

    }
}