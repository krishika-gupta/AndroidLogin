package com.clicklabs.androidlogin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clicklabs.androidlogin.UserDatabase;
import com.clicklabs.androidlogin.Utils.CommonData;
import com.clicklabs.androidlogin.R;
import com.clicklabs.androidlogin.Models.UserInformation;
import com.squareup.picasso.Picasso;

/**
 * Created by hp- on 11-03-2016.
 */
public class NextPageActivity extends BaseActivity implements View.OnClickListener {
    EditText etGender, etPhone, etAddress;
    Button btnSubmit, btnLogout;
    String gender, phoneNo, address;
    ImageView profilePictureView;
    String currentIntent;
    UserInformation userInformation;
    UserDatabase userDatabase;
    TextView toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_page_layout);
        init();

        currentIntent = getIntent().getAction();
        if (currentIntent.equals("FACEBOOK")) {
            etGender.setEnabled(false);
        } else {
            etGender.setEnabled(true);
        }
        userInformation = CommonData.getUserInfo(NextPageActivity.this);
        checkForProfile(userInformation);
        btnLogout.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    public void init() {
        etGender = (EditText) findViewById(R.id.edt_gender);
        etPhone = (EditText) findViewById(R.id.edt_phone);
        etAddress = (EditText) findViewById(R.id.edt_address);
        toolbar=(TextView)findViewById(R.id.action);
        profilePictureView = (ImageView) findViewById(R.id.fb_pic);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnLogout = (Button) findViewById(R.id.btn_log_out);
        toolbar.setText("Details");
    }

    private void checkForProfile(UserInformation profile) {
        if (profile != null) {
            Picasso.with(this)
                    .load(profile.getPic())
                    .into(profilePictureView);
            etGender.setText(profile.getGender());
        }
    }

    private void getValue() {
        gender = etGender.getText().toString();
        phoneNo = etPhone.getText().toString();
        address = etAddress.getText().toString();

        userInformation.setGender(gender);
        userInformation.setMobileNo(phoneNo);
        userInformation.setAddress(address);

    }

    private boolean isValid() {


        if (gender.isEmpty())

        {
            etGender.setError("enter gender field");

            return false;
        }
        if (phoneNo.isEmpty()&&phoneNo.length()<10)

        {
            etPhone.setError("enter valid mobile no");


            return false;
        }

        if (address.isEmpty()) {
            etAddress.setError("enter valid address");
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                getValue();

                if (isValid()) {
                    // Log.v("TAG SUBMIT", Tag);
                    userDatabase = new UserDatabase(getApplicationContext());
                    userDatabase.addUser(userInformation);
                    Log.v("TAG Database", userDatabase.getContactsCount() + "");

                    Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Added to db", Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.btn_log_out:
                CommonData.clearAllAppData();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
        }
    }
}



