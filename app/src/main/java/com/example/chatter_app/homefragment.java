package com.example.chatter_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

public class homefragment extends Fragment {

    private ViewPager2 viewPager2;
    private TabLayout table1;
    private Toolbar toolbar;  // Declare the Toolbar variable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_homefragment, container, false);

        // Set the status bar color (access Activity window)
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.status)); // Ensure you have R.color.status defined
        }

        // Initialize TabLayout and ViewPager2
        table1 = rootView.findViewById(R.id.table1);  // TabLayout from your XML
        viewPager2 = rootView.findViewById(R.id.viewPager2);  // ViewPager2 from your XML
        toolbar = rootView.findViewById(R.id.toolbar);  // Reference to your toolbar

        // Set up the adapter for ViewPager2
        adaptor adaptor1 = new adaptor(getActivity());  // Create the Adapter
        viewPager2.setAdapter(adaptor1);  // Set the adapter to ViewPager2

        // Link the TabLayout and ViewPager2 together using TabLayoutMediator
        new TabLayoutMediator(table1, viewPager2, (tab, position) -> {
            // Set the title for each tab
            tab.setText(adaptor1.getPageTitle(position));
        }).attach();

        // Set the toolbar as the ActionBar
        if (getActivity() != null) {
            androidx.appcompat.app.ActionBar actionBar = ((androidx.appcompat.app.AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);  // To show the back button, if required
            }
            ((androidx.appcompat.app.AppCompatActivity) getActivity()).setSupportActionBar(toolbar);  // Set the toolbar as the action bar
        }

        // Enable options menu for the fragment
        setHasOptionsMenu(true); // Make sure to enable the menu for the fragment
        return rootView;
    }

    // Inflate the toolbar menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate your menu here
        inflater.inflate(R.menu.toolbar_menu, menu); // Reference to your menu XML
    }

    // Handle item selection from the menu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handling the item selection
        switch (item.getItemId()) {
            case 1 :
                int actionSearch = R.id.action_search;// Correct ID as defined in the menu XML
// Handle the search image click action
                return true;

            case 2:
                int actionMore = R.id.action_more;
                // Correct ID as defined in the menu XML
                // Handle the more image click action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
