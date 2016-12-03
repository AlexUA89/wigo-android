package com.wigo.android.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.ui.elements.CategoriesProvider;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

public class CategoryActivity extends Activity {

    private TreeMap<String, Bitmap> categoriesWithImages = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }
    });
    private ListView categoryList;
    private Set<String> choosedCategpries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        setTitle(getString(R.string.category_activity_title));

        categoriesWithImages.putAll(CategoriesProvider.getMapOfCategoriesAndImages());
        choosedCategpries = CategoriesProvider.getChoosenCategories();

        categoryList = (ListView) findViewById(R.id.category_list);
        categoryList.setAdapter(new CategoryAdapter());

        findViewById(R.id.categories_ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okButtonPressed();
            }
        });
        findViewById(R.id.categories_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelButtonPressed();
            }
        });

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cBox = ((CheckBox) view.findViewById(R.id.category_item_checkbox));
                String categoryName = (String) categoriesWithImages.keySet().toArray()[position];
                if(cBox.isChecked()) {
                    choosedCategpries.remove(categoryName);
                } else {
                    choosedCategpries.add(categoryName);
                }
                cBox.setChecked(!cBox.isChecked());
            }
        });
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
            String categoryName = (String) categoriesWithImages.keySet().toArray()[position];
            ((TextView) view.findViewById(R.id.category_item_text)).setText(categoryName);
            ((ImageView) view.findViewById(R.id.category_item_image_view)).setImageBitmap(((Bitmap) categoriesWithImages.values().toArray()[position]));
            CheckBox cBox = ((CheckBox) view.findViewById(R.id.category_item_checkbox));
            cBox.setChecked(choosedCategpries.contains(categoryName));
            convertView = view;
            return convertView;
        }
    }

    private void okButtonPressed() {
        CategoriesProvider.setChoosenCategories(choosedCategpries);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void onCancelButtonPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onCancelButtonPressed();
    }
}
