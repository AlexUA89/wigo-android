package com.wigo.android.ui.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.model.LatLng;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.StatusCategory;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.requestapi.errors.WigoException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by AlexUA89 on 12/3/2016.
 */

public class CreateEventActivity extends Activity {

    public static final String CREATED_STATUS = "CREATED_STATUS";
    public static final String POINT = "POINT";

    private static final int PICK_CATEGORIES = 1;
    private EditText eventName, eventDescription;
    private LatLng point;
    private StatusDto newStatus;
    private CreateStatusRequest task;
    private Button okButton, cancelButton, chooseCategory, fromDateButton, toDateButton;
    private Calendar fromDate, toDate;
    private StatusCategory category = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_status_activity);
        setTitle(getString(R.string.create_status_title));
        Bundle b = this.getIntent().getExtras();
        if (b != null) point = b.getParcelable(POINT);
        eventName = (EditText) findViewById(R.id.create_event_name);
        eventDescription = (EditText) findViewById(R.id.create_event_description);

        chooseCategory = (Button) findViewById(R.id.create_event_choose_category);
        chooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseStatusButtonPressed();
            }
        });

        okButton = (Button) findViewById(R.id.create_status_ok_button);
        cancelButton = (Button) findViewById(R.id.create_status_cancel_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okButtonPressed();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelButtonPressed();
            }
        });

        fromDateButton = (Button) findViewById(R.id.create_event_from_date_button);
        toDateButton = (Button) findViewById(R.id.create_event_to_date_button);
        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();
        fromDate.set(fromDate.get(Calendar.YEAR), fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        toDate.set(toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        toDate.add(Calendar.DATE, 1);
        redrawDateButtons();
        final Activity activity = this;

        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        fromDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        redrawDateButtons();
                    }
                }, fromDate.get(Calendar.YEAR), fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(toDate.getTimeInMillis());
                dialog.show();
            }
        });
        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        toDate.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
                        redrawDateButtons();
                    }
                }, toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(fromDate.getTimeInMillis());
                dialog.show();
            }
        });
    }


    private void okButtonPressed() {
        String name = eventName.getText().toString();
        if (name == null || name.isEmpty()) {
            Toast.makeText(ContextProvider.getAppContext(), ContextProvider.getAppContext().getString(R.string.create_status_empty_name), Toast.LENGTH_LONG).show();// display toast
            return;
        }
        if (category == null) {
            Toast.makeText(ContextProvider.getAppContext(), ContextProvider.getAppContext().getString(R.string.create_status_empty_category), Toast.LENGTH_LONG).show();// display toast
            return;
        }
        newStatus = new StatusDto();
        newStatus.setName(name);
        newStatus.setStartDate(fromDate.getTime());
        newStatus.setEndDate(toDate.getTime());
        newStatus.setCategory(category);
        newStatus.setLatitude(point.latitude);
        newStatus.setLongitude(point.longitude);
        newStatus.setText(eventDescription.getText().toString());
        newStatus.setHashtags(Collections.EMPTY_LIST);
        String userId = SharedPrefHelper.getUserId(null);
        if (userId != null) {
            newStatus.setUserId(UUID.fromString(userId));
        }
        task = new CreateStatusRequest();
        task.execute();
    }

    private void onCancelButtonPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void redrawDateButtons() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        fromDateButton.setText(format.format(fromDate.getTime()));
        toDateButton.setText(format.format(toDate.getTime()));
    }

    @Override
    public void onBackPressed() {
        onCancelButtonPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CATEGORIES && resultCode == RESULT_OK) {
            HashSet<String> status = (HashSet<String>) data.getExtras().get(CategoryActivity.CHOOSED_CATEGORIES);
            if (!status.isEmpty()) {
                category = StatusCategory.valueOf(status.iterator().next());
                chooseCategory.setText(category.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void choseStatusButtonPressed() {
        Intent pickContactIntent = new Intent(ContextProvider.getAppContext(), CategoryActivity.class);
        pickContactIntent.putExtra(CategoryActivity.IS_MULTIPLE, false);
        pickContactIntent.putExtra(CategoryActivity.LOAD_CATEGORIES, false);
        startActivityForResult(pickContactIntent, PICK_CATEGORIES);
    }

    class CreateStatusRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            okButton.setEnabled(false);
            cancelButton.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ContextProvider.getWigoRestClient().createNewChat(newStatus);
            } catch (final WigoException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ContextProvider.getAppContext(), ContextProvider.getAppContext().getString(R.string.create_status_connection_error) + " " + e.getMessage(), Toast.LENGTH_SHORT).show();// display toast
                    }
                });
                this.cancel(true);
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            okButton.setEnabled(true);
            cancelButton.setEnabled(true);
            Intent returnIntent = new Intent();
            try {
                returnIntent.putExtra(CREATED_STATUS, ContextProvider.getObjectMapper().writeValueAsString(newStatus));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        @Override
        protected void onCancelled(Void aVoid) {
            okButton.setEnabled(true);
            cancelButton.setEnabled(true);
            super.onCancelled(aVoid);
        }
    }

}
