package com.wigo.android.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.model.LatLng;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.dto.StatusKind;

import org.springframework.web.client.HttpClientErrorException;

import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by AlexUA89 on 12/3/2016.
 */

public class CreateStatusActivity extends Activity {

    public static final String CREATED_STATUS = "CREATED_STATUS";
    public static final String POINT = "POINT";

    private EditText editText;
    private LatLng point;
    private StatusDto newStatus;
    private CreateStatsRequest task;
    private Button okButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_status_activity);
        setTitle(getString(R.string.create_status_title));
        Bundle b = this.getIntent().getExtras();
        if(b!=null) point = b.getParcelable(POINT);
        editText = (EditText) findViewById(R.id.create_status_edittext);

        okButton = (Button)findViewById(R.id.create_status_ok_button);
        cancelButton = (Button)findViewById(R.id.create_status_cancel_button);


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
    }


    private void okButtonPressed() {
        String name = editText.getText().toString();
        if(name == null || name.isEmpty()) {
            Toast.makeText(ContextProvider.getAppContext(), ContextProvider.getAppContext().getString(R.string.create_status_empty_name), Toast.LENGTH_SHORT).show();// display toast
            return;
        }
        newStatus = new StatusDto();
        newStatus.setName(name);
        Calendar fromDate = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(fromDate.getTimeInMillis());
        c.add(Calendar.DATE, 1);
        Calendar toDate = SharedPrefHelper.getToDateSearch(c);
        newStatus.setStartDate(fromDate.getTime());
        newStatus.setEndDate(toDate.getTime());
        newStatus.setKind(StatusKind.chat.toString());
        newStatus.setLatitude(point.latitude);
        newStatus.setLongitude(point.longitude);
        newStatus.setText("");
        newStatus.setCategory("OTHER");
        newStatus.setHashtags(Collections.EMPTY_LIST);
        newStatus.setUserId(UUID.fromString(SharedPrefHelper.getUserId(null)));

        task = new CreateStatsRequest();
        task.execute();


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

    class CreateStatsRequest extends AsyncTask<Void, Void, Void> {

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
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
                Toast.makeText(ContextProvider.getAppContext(), ContextProvider.getAppContext().getString(R.string.create_status_connection_error), Toast.LENGTH_SHORT).show();// display toast
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
