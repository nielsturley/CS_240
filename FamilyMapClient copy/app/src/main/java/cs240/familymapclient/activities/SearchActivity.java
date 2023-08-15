package cs240.familymapclient.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.List;

import cs240.familymapclient.DataCache;
import cs240.familymapclient.R;
import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PEOPLE_VIEW_TYPE = 0;
    private static final int EVENT_VIEW_TYPE = 1;
    private List<Person> people;
    private List<Event> events;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        people = new ArrayList<>();
        events = new ArrayList<>();
        adapter = new SearchAdapter(people, events);

        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, //because we like pretty things
                linearLayoutManager.getOrientation());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

        Iconify.with(new FontAwesomeModule());

        SearchView searchView = findViewById(R.id.search_bar_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.isEmpty()) {
                    search(s);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> people;
        private final List<Event> events;

        SearchAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PEOPLE_VIEW_TYPE : EVENT_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == PEOPLE_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView icon;
        private final TextView name;
        private final TextView eventInfo;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == PEOPLE_VIEW_TYPE) {
                icon = itemView.findViewById(R.id.gender_icon_person_item);
                name = itemView.findViewById(R.id.name_textview_person_item);
                //does not set eventInfo (duh, not an event). Also does not set relationship textview
                // (see xml) because this is not a PersonActivity.
                eventInfo = null;
            } else {
                icon = itemView.findViewById(R.id.marker_icon_event_item);
                name = itemView.findViewById(R.id.name_associated_textview_event_item);
                eventInfo = itemView.findViewById(R.id.event_info_textview_event_item);
            }
        }

        private void bind(Person person) {
            this.person = person;
            if (person.getGender().equals("m")) {
                icon.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male)
                        .color(Color.DKGRAY));
            } else {
                icon.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female)
                        .color(Color.DKGRAY));
            }
            String name = person.getFirstName() + " " + person.getLastName();
            this.name.setText(name);
        }

        private void bind(Event event) {
            this.event = event;
            icon.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker)
                .color(Color.DKGRAY));
            Person person = DataCache.getInstance().getPersonByID(event.getPersonID());

            String name = person.getFirstName() + " " + person.getLastName();
            this.name.setText(name);

            String eventInfo = event.getEventType() +
                    ": " +
                    event.getCity() +
                    ", " +
                    event.getCountry() +
                    " (" +
                    event.getYear() +
                    ")";
            this.eventInfo.setText(eventInfo);
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            if (viewType == PEOPLE_VIEW_TYPE) {
                intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PersonActivity.PERSON_KEY, person.getPersonID());
            } else {
                intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(EventActivity.EVENT_KEY, event.getEventID());
            }
            startActivity(intent);
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

    private void search(String query) {
        people.clear();
        events.clear();
        DataCache dataCache = DataCache.getInstance();
        people.addAll(dataCache.searchPeople(query));
        events.addAll(dataCache.searchEvents(query));
    }
}