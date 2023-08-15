package cs240.familymapclient.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import cs240.familymapclient.fragments.LoginFragment;
import cs240.familymapclient.fragments.MapFragment;
import cs240.familymapclient.R;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container_main);

        if (fragment == null) {
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.registerListener(this);
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container_main, loginFragment)
                    .commit();
        } else {
            if (fragment instanceof LoginFragment) {
                ((LoginFragment) fragment).registerListener(this);
            }
        }
    }

    @Override
    public void notifyDone() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment mapFragment = new MapFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_main, mapFragment)
                .commit();
    }
}