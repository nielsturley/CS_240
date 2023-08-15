package cs240.familymapclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import cs240.familymapclient.DataCache;
import cs240.familymapclient.R;
import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    public static final String PERSON_KEY = "PersonKey";

    private Person personOnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        TextView firstName = findViewById(R.id.selected_person_first_name);
        TextView lastName = findViewById(R.id.selected_person_last_name);
        TextView gender = findViewById(R.id.selected_person_gender);

        Intent intent = getIntent();
        String receivedPersonID = intent.getStringExtra(PERSON_KEY);
        personOnView = DataCache.getInstance().getPersonByID(receivedPersonID);

        //set textview fields
        firstName.setText(personOnView.getFirstName());
        lastName.setText(personOnView.getLastName());
        if (personOnView.getGender().equals("m")) {
            gender.setText(R.string.male);
        }
        else {
            gender.setText(R.string.female);
        }



        DataCache dataCache = DataCache.getInstance();

        List<Person> family = dataCache.getPersonFamily(personOnView.getPersonID());
        List<Event> events = dataCache.getPersonEvents(personOnView.getPersonID());
        //this checks and removes all events that aren't "on display" aka are being filtered out rn
        List<Event> eventsNotDisplayed = new ArrayList<>();
        for (Event e : events) {
            if (!dataCache.isEventOnDisplay(e)) {
                eventsNotDisplayed.add(e);
            }
        }
        events.removeAll(eventsNotDisplayed);

        ExpandableListView expandableListView = findViewById(R.id.expandable_list_view_person);
        expandableListView.setAdapter(new ExpandableListAdapter(family, events));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int FAMILY_GROUP_POSITION = 0;
        private static final int EVENTS_GROUP_POSITION = 1;

        private final List<Person> family;
        private final List<Event> events;

        ExpandableListAdapter(List<Person> family, List<Event> events) {
            this.family = family;
            this.events = events;
        }

        @Override
        public int getGroupCount() { return 2; }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case FAMILY_GROUP_POSITION:
                    return family.size();
                case EVENTS_GROUP_POSITION:
                    return events.size();
                default:
                    return -1; //throwing exceptions (like the example) wasn't working? so I returned
                                //a bad number instead
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case FAMILY_GROUP_POSITION:
                    return getString(R.string.people_title);
                case EVENTS_GROUP_POSITION:
                    return getString(R.string.events_title);
                default:
                    return getString(R.string.error);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case FAMILY_GROUP_POSITION:
                    return family.get(childPosition);
                case EVENTS_GROUP_POSITION:
                    return events.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
                    //why does it let me throw one here? Don't know. Luckily I'll never need to
                    //throw it, eh?
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.list_title);

            switch (groupPosition) {
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.people_title);
                    break;
                case EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.events_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                case EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializePersonView(View personItemView, final int childPosition) {
            Person person = family.get(childPosition);

            ImageView icon = personItemView.findViewById(R.id.gender_icon_person_item);
            if (person.getGender().equals("m")) {
                icon.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male)
                        .color(Color.DKGRAY));
            } else {
                icon.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female)
                        .color(Color.DKGRAY));
            }

            //set name textview
            TextView name = personItemView.findViewById(R.id.name_textview_person_item);
            String s = person.getFirstName() + " " + person.getLastName();
            name.setText(s);

            //set relationship textview
            TextView relationship = personItemView.findViewById(R.id.relationship_textview_person_item);
            relationship.setText(DataCache.getInstance().getRelationship(personOnView, person));


            personItemView.setOnClickListener(view -> {
                Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                intent.putExtra(PERSON_KEY, person.getPersonID());
                startActivity(intent);
            });
        }

        private void initializeEventView(View eventItemView, final int childPosition) {
            Event event = events.get(childPosition);

            ImageView icon = eventItemView.findViewById(R.id.marker_icon_event_item);
            icon.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker)
                    .color(Color.DKGRAY));

            //set associated name textview
            TextView name = eventItemView.findViewById(R.id.name_associated_textview_event_item);
            Person person = DataCache.getInstance().getPersonByID(event.getPersonID());
            String s1 = person.getFirstName() + " " + person.getLastName();
            name.setText(s1);

            //set event info textview
            TextView eventInfo = eventItemView.findViewById(R.id.event_info_textview_event_item);
            String s2 = event.getEventType() +
                    ": " +
                    event.getCity() +
                    ", " +
                    event.getCountry() +
                    " (" +
                    event.getYear() +
                    ")";
            eventInfo.setText(s2);

            eventItemView.setOnClickListener(view -> {
                Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                intent.putExtra(EventActivity.EVENT_KEY, event.getEventID());
                startActivity(intent);
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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