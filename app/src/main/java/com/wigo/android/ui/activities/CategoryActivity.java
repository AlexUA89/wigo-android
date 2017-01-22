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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.StatusCategory;
import com.wigo.android.ui.elements.CategoriesProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class CategoryActivity extends Activity {

    List<String> categoriesWithImages = new ArrayList<>();
    private ListView categoryList;
    private HashSet<String> choosedCategories;

    private boolean allSelected;
    private Button selectAllButton;

    private boolean multipleChoise = true;
    public static final String IS_MULTIPLE = "IS_MULTIPLE";

    private boolean loadCategories = true;
    public static final String LOAD_CATEGORIES = "LOAD_CATEGORIES";

    public static final String CHOOSED_CATEGORIES = "CHOOSED_CATEGORIES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        setTitle(getString(R.string.category_activity_title));

        if(getIntent().getExtras()!=null) {
            multipleChoise  = getIntent().getExtras().get(IS_MULTIPLE) !=null ? (Boolean) getIntent().getExtras().get(IS_MULTIPLE) : true;
            loadCategories  = getIntent().getExtras().get(LOAD_CATEGORIES) !=null ? (Boolean) getIntent().getExtras().get(LOAD_CATEGORIES) : true;
        }

        TreeMap<String, Bitmap> temp = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        temp.putAll(CategoriesProvider.getMapOfCategoiesAndBitmaps());
        temp.remove(StatusCategory.CHAT.toString());
        temp.remove(StatusCategory.ACTIVITY.toString());
        temp.remove(StatusCategory.CELEBRATION.toString());
        categoriesWithImages.addAll(temp.keySet());
        categoriesWithImages.add(0,StatusCategory.CELEBRATION.toString());
        categoriesWithImages.add(0,StatusCategory.ACTIVITY.toString());
        categoriesWithImages.add(0,StatusCategory.CHAT.toString());

        if(loadCategories) {
            choosedCategories = CategoriesProvider.getChoosenCategories();
        } else {
            choosedCategories = new HashSet<>();
        }

        categoryList = (ListView) findViewById(R.id.category_list);
        categoryList.setAdapter(new CategoryAdapter());

        allSelected = Math.round((double) choosedCategories.size() / (double) categoriesWithImages.size()) == 1l;

        selectAllButton = (Button) findViewById(R.id.categories_all_button);
        if(multipleChoise) {
            updateSelectAllButtonText();
            selectAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectAllButtonPressed();
                }
            });
        } else {
            selectAllButton.setVisibility(View.GONE);
        }

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
                String categoryName = categoriesWithImages.get(position);
                if(multipleChoise) {
                    if (cBox.isChecked()) {
                        choosedCategories.remove(categoryName);
                    } else {
                        choosedCategories.add(categoryName);
                    }
                    cBox.setChecked(!cBox.isChecked());
                } else {
                    choosedCategories.clear();
                    choosedCategories.add(categoryName);
                    ((BaseAdapter) categoryList.getAdapter()).notifyDataSetChanged();
                }
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
            return categoriesWithImages.size();
        }

        @Override
        public Object getItem(int position) {
            return categoriesWithImages.toArray()[position];
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
            String categoryName = categoriesWithImages.get(position);
            ((TextView) view.findViewById(R.id.category_item_text)).setText(categoryName);
            ((ImageView) view.findViewById(R.id.category_item_image_view)).setImageBitmap((CategoriesProvider.getMapOfCategoiesAndBitmaps().get(categoryName)));
            CheckBox cBox = ((CheckBox) view.findViewById(R.id.category_item_checkbox));
            cBox.setChecked(choosedCategories.contains(categoryName));
            convertView = view;
            return convertView;
        }
    }

    private void okButtonPressed() {
        if(loadCategories) {
            CategoriesProvider.setChoosenCategories(choosedCategories);
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra(CHOOSED_CATEGORIES, choosedCategories);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void onCancelButtonPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void selectAllButtonPressed() {
        allSelected = !allSelected;
        if (allSelected) {
            choosedCategories = new HashSet<>(categoriesWithImages);
        } else {
            choosedCategories.clear();
        }
        ((BaseAdapter) categoryList.getAdapter()).notifyDataSetChanged();
        updateSelectAllButtonText();
    }

    private void updateSelectAllButtonText() {
        if (allSelected) {
            selectAllButton.setText(getResources().getString(R.string.category_activity_button_all_deselect));
        } else {
            selectAllButton.setText(getResources().getString(R.string.category_activity_button_all_select));
        }
    }

    @Override
    public void onBackPressed() {
        onCancelButtonPressed();
    }
}
