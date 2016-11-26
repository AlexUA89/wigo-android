package com.wigo.android.ui.slidingmenu;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.database.DBManager;
import com.wigo.android.core.database.Database;
import com.wigo.android.core.database.datas.Status;
import com.wigo.android.core.server.dto.StatusKind;
import com.wigo.android.core.utils.BitmapUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olkh on 11/13/2015.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private List<Status> statuses = new ArrayList<>();

    public NavDrawerListAdapter() throws ParseException {
        loadData();
    }

    private void loadData() throws ParseException {
        Database db = DBManager.getDatabase();
        db.open();
        Cursor c = db.selectAllLastActiveStatuses();
        if (c.moveToFirst()) {
            while (c.isAfterLast() == false) {
                statuses.add(new Status(c));
                c.moveToNext();
            }
        }
        db.close();
    }

    public void updateMenuItems() throws ParseException {
        statuses.clear();
        loadData();
        notifyDataSetChanged();
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
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    ContextProvider.getAppContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.menu_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        txtTitle.setText(statuses.get(position).getName());

        // displaying count
        // check whether it set visible or not
        txtCount.setVisibility(View.GONE);
        if (StatusKind.chat.toString().equals(statuses.get(position).getKind())) {
            imgIcon.setImageResource(R.mipmap.chat);
        } else {
            imgIcon.setImageResource(R.mipmap.other);
        }
        txtCount.setVisibility(View.GONE);
        return convertView;
    }

}
