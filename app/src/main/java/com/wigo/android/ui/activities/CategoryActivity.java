package com.wigo.android.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.ui.elements.CategoriesProvider;

import java.util.Comparator;
import java.util.TreeMap;

public class CategoryActivity extends Activity {

    private TreeMap<String, Bitmap> categoriesWithImages = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }
    });
    private ListView categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        categoriesWithImages.putAll(CategoriesProvider.getMapOfCategoriesAndImages());
        setTitle(getString(R.string.category_activity_title));


        categoryList = (ListView) findViewById(R.id.category_list);
        categoryList.setAdapter(new CategoryAdapter());

    }

    class CategoryAdapter extends BaseAdapter {

        private LayoutInflater lInflater;

        public CategoryAdapter() {
            lInflater = (LayoutInflater) ContextProvider.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return categoriesWithImages.keySet().size();
        }

        @Override
        public Object getItem(int position) {
            return categoriesWithImages.keySet().toArray()[position];
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
                view = lInflater.inflate(R.layout.category_list_item, parent, false);
            }
            ((TextView) view.findViewById(R.id.category_item_text)).setText((String) categoriesWithImages.keySet().toArray()[position]);
            ((ImageView) view.findViewById(R.id.category_item_image_view)).setImageBitmap(((Bitmap)categoriesWithImages.values().toArray()[position]));
            convertView = view;
            return convertView;
        }


    }


}
