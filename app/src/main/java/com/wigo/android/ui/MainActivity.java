package com.wigo.android.ui;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.database.DBManager;
import com.wigo.android.core.database.datas.Status;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.ui.fragments.ChatFragment;
import com.wigo.android.ui.fragments.MapFragment;
import com.wigo.android.ui.slidingmenu.NavDrawerListAdapter;

import java.text.ParseException;

public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private MapFragment mapFragment;
    private ChatFragment chatFragment;
    private MainActivity mainActivity;
    private NavDrawerListAdapter adapter;
    private Menu menu;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        requestPermissions();
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.main_activity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        try {
            adapter = new NavDrawerListAdapter();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(ContextProvider.getAppContext(), "Can not load statuses list", Toast.LENGTH_SHORT).show();// display toast
        }
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(chatFragment.getStatus().getName());
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle("Menu");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (mapFragment == null) {
//            fragment = new MapFragment();
            mapFragment = new MapFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mapFragment).commit();
        }
        if (getIntent().getAction() != null && getIntent().getData() != null) {
            handleUri(getIntent().getData());
        }

    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position,
                                long id) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Status status = (Status) adapter.getItem(position);
                    StatusDto statusDto = ContextProvider.getWigoRestClient().getStatusById(status.getId());
                    if (statusDto != null) {
                        openChatFragment(statusDto);
                    } else {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ContextProvider.getAppContext(), "Can not find such event on server", Toast.LENGTH_SHORT).show();// display toast
                            }
                        });
                    }
//            mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.FRAGMENT_TAG);
//            openMapFragment();
                    // display view for selected nav drawer item
//            displayView(position);
                }
            }).start();
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        menu.findItem(R.id.share_menu_item).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_database_to_sd) {
            DBManager.saveDBonSDCard();
//            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.FRAGMENT_TAG);
//            if(mapFragment!=null){
//                getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
//            }
//            return true;
        }
        if (id == R.id.share_menu_item) {
            if (chatFragment != null) {
                chatFragment.onShareButtonClick();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public void openChatFragment(final StatusDto statusDto) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatFragment == null || !chatFragment.getStatus().equals(statusDto)) {
                    chatFragment = new ChatFragment();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActionBar().setTitle(statusDto.getName());
                        }
                    });
                    Bundle args = new Bundle();
                    try {
                        args.putString(ChatFragment.STATUS_DTO, ContextProvider.getObjectMapper().writeValueAsString(statusDto));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    chatFragment.setArguments(args);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment, ChatFragment.FRAGMENT_TAG).addToBackStack(null).commit();
                menu.findItem(R.id.share_menu_item).setVisible(true);
            }
        });
    }

    public void openMapFragment() {
        //deleting chat fragment
        if (chatFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
            chatFragment = null;
        }
        if (mapFragment == null) {
            mapFragment = new MapFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment, MapFragment.FRAGMENT_TAG).addToBackStack(null).commit();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDrawerList.clearChoices();
                getActionBar().setTitle(R.string.app_name);
            }
        });
        menu.findItem(R.id.share_menu_item).setVisible(false);
    }

    @Override
    public void onBackPressed() {
        if (mapFragment == null || !mapFragment.isVisible()) {
            openMapFragment();
        } else {
            finish();
        }
    }

    public void updateMenuList() {
        try {
            adapter.updateMenuItems();
            mDrawerList.clearChoices();
            mDrawerList.setItemChecked(0, true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    private void handleUri(Uri data) {
        System.out.println("OPPENED URI !!!!!!!!!!!!!!!!!!!!!!!!!!!! " + data);
    }
}
