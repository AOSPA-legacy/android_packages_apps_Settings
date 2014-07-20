package com.android.settings.paranoid;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

// Extensions 
import com.android.settings.paranoid.extensions.CustomDrawerLayout;
import com.android.settings.paranoid.extensions.NavDrawerListAdapter;
import com.android.settings.paranoid.extensions.NavDrawerItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

//============================
// classes for implements
//============================

//CM classes
import com.android.settings.cyanogenmod.ButtonSettings;
import com.android.settings.cyanogenmod.PerformanceSettings;

//AOSPA-Legacy classes
import com.android.settings.paranoid.DisplaySettingsLP;
import com.android.settings.paranoid.GeneralLP;

import java.util.ArrayList;

public class LegacyParts extends SettingsPreferenceFragment {

    //==================================
    // Drawer
    //==================================
    private ActionBarDrawerToggle mDrawerToggle;
    private CustomDrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private String[] mListTitles;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private NavDrawerListAdapter adapter;
    private int position;
    private int mCurrentSelectedPosition = 0;
    //==================================
    //LP Classes
    //==================================
    private static final int General = 0;
    private static final int Display = 1;
    private static final int BtnSettings = 2;
    private static final int PerfSettings = 3;
    
    private GeneralLP mGeneral;
    private DisplaySettingsLP mDisplay;
    private ButtonSettings mBtnSettings;
    private PerformanceSettings mPerfSettings;

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private AlertDialog alertDialog;    
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) { 
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView;

        rootView = (ViewGroup) inflater.inflate(R.layout.legacy_parts, container, false);
        mListTitles = getResources().getStringArray(R.array.lp_titles_array); // location of drawer list titles array
        this.mListTitles = mListTitles;
        mDrawerListView = (ListView) rootView.findViewById(R.id.drawer_list_lp);

        // adding nav drawer icons to drawer list array
        TypedArray navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons); // location of drawer list imageview array

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        assert navMenuIcons != null;
        navDrawerItems.add(new NavDrawerItem(mListTitles[0], navMenuIcons.getResourceId(0, -1))); //General
        navDrawerItems.add(new NavDrawerItem(mListTitles[1], navMenuIcons.getResourceId(1, -1))); //Display
        navDrawerItems.add(new NavDrawerItem(mListTitles[2], navMenuIcons.getResourceId(2, -1))); //Buttons
        navDrawerItems.add(new NavDrawerItem(mListTitles[3], navMenuIcons.getResourceId(3, -1))); //Performance

        // Recycle the typed array
        navMenuIcons.recycle();

        adapter = new NavDrawerListAdapter(getActionBar().getThemedContext(),
                navDrawerItems);
        mDrawerListView.setAdapter(adapter);

        mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        setUpNavigationDrawer(
                rootView.findViewById(R.id.drawer_list_lp),
                (CustomDrawerLayout) rootView.findViewById(R.id.drawer_layout_lp));

        if (container instanceof PreferenceFrameLayout) {
            ((PreferenceFrameLayout.LayoutParams) rootView.getLayoutParams()).removeBorders = true;
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.legacy_parts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.toggle_drawer:
                if (isDrawerOpen())
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                else
                    mDrawerLayout.openDrawer(mFragmentContainerView);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        //Hide toggle unless drawer is open
        menu.findItem(R.id.toggle_drawer).setVisible(isDrawerOpen());
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    /**
     * Users of this fragment must call this method to set up the
     * navigation menu_drawer interactions.
     *
     * @param fragmentContainerView The view of this fragment in its activity's layout.
     * @param drawerLayout          The DrawerLayout containing this fragment's UI.
     */
    private void setUpNavigationDrawer(View fragmentContainerView, CustomDrawerLayout drawerLayout) {
        mFragmentContainerView = fragmentContainerView;
        mDrawerLayout = drawerLayout;
        mTitle = mDrawerTitle = getActivity().getTitle();

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                updateActionBarTitles();
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActivity().setTitle(mDrawerTitle);
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }         
        };

        // Remove or set it to true, if you want to use home to toggle the menu_drawer
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        selectItem(mCurrentSelectedPosition);
    }

    private boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    // Populating drawer list with correcting positiong of classes
    private void selectItem(int position) {
        this.position = position;
        SettingsPreferenceFragment fragment = null;
        switch (position) {
            case General:
                if (mGeneral == null)
                    mGeneral = new GeneralLP();
                fragment = mGeneral;
                break;
            case Display:
                if (mDisplay == null)
                    mDisplay = new DisplaySettingsLP();
                fragment = mDisplay;
                break;
            case BtnSettings:
                if (mBtnSettings == null)
                    mBtnSettings = new ButtonSettings();
                fragment = mBtnSettings;
                break;
            case PerfSettings:
                if (mPerfSettings == null)
                    mPerfSettings = new PerformanceSettings();
                fragment = mPerfSettings;
                break;
            default:
                break;
        }

        updateActionBarTitles();

        if (fragment != null) {
            FragmentManager frgManager = getFragmentManager();
            frgManager.beginTransaction().replace(R.id.container_lp, fragment)
                    .commit();

            mDrawerListView.setItemChecked(position, true);
            mDrawerListView.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerListView);
        } else {
            /*
             * if there is a error creating a fragment/nav drawer item,
             * create a log output for debugging purposes
             */
            Log.e("LegacyParts", "Error in creating fragment");
        }
    }

    private void updateActionBarTitles() {
        if(position == General) {
            getActivity().setTitle(mListTitles[0]);
        } else if (position == Display) {
            getActivity().setTitle(mListTitles[1]);
        } else if (position == BtnSettings) {
            getActivity().setTitle(mListTitles[2]);
        } else if (position == PerfSettings) {
            getActivity().setTitle(mListTitles[3]);
        }
    }
}
