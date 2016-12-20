package com.wigo.android.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.ui.elements.CategoriesProvider;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by AlexUA89 on 12/20/2016.
 */

public class StatusListActivity extends Activity {

    public static final String STATUSES = "STATUSES";
    public static final String CHOOSED_STATUS = "CHOOSED_STATUS";

    private ListView statusesList;
    private List<StatusDto> statuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.status_list_activity_title));
        setContentView(R.layout.status_list_activity);

        statuses = (List<StatusDto>) getIntent().getExtras().get(STATUSES);
        Collections.sort(statuses, new Comparator<StatusDto>() {
            @Override
            public int compare(StatusDto lhs, StatusDto rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        statusesList = (ListView) findViewById(R.id.status_list);
        statusesList.setAdapter(new StatusListActivity.StatusListAdapter());

        statusesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseStatus(statuses.get(position));
            }
        });
    }


    class StatusListAdapter extends BaseAdapter {

        private LayoutInflater lInflater;

        public StatusListAdapter() {
            lInflater = (LayoutInflater) ContextProvider.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return statuses.size();
        }

        @Override
        public Object getItem(int position) {
            return statuses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;
            if (convertView == null) {
                view = lInflater.inflate(R.layout.status_list_item, parent, false);
            }
            ((TextView) view.findViewById(R.id.status_item_text)).setText(statuses.get(position).getName());
            ((ImageView) view.findViewById(R.id.status_item_image_view)).setImageBitmap(CategoriesProvider.getMapOfCategoriesAndImages().get(statuses.get(position).getCategory()));
            convertView = view;
            return convertView;
        }
    }

    private void chooseStatus(StatusDto statusDto) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(CHOOSED_STATUS, statusDto);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
