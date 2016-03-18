package com.clicklabs.androidlogin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clicklabs.androidlogin.Utils.CommonData;
import com.clicklabs.androidlogin.R;
import com.clicklabs.androidlogin.Models.UserInformation;
import com.clicklabs.androidlogin.Utils.CommonProgress;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.Scope;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    LoginButton facebookLoginbutton;
    private TwitterLoginButton twitterLoginButton;
    SignInButton googleLoginButton;
    Button linkedinLoginButton;
    UserInformation userInformation;

    CallbackManager facebookCallbackManager;
    EditText etFirstName, etLastName, etEmail;
    String firstName, lastName, email, gender, profilePic,currentIntent;
    Button btnNext,btnLogout;
    TextView toolbar;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:" +
            "(email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
    private static final String Url = "https://" + host + "/v1/people/~:" +
            "(email-address,firstName,lastName,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        facebookCallbackManager = CallbackManager.Factory.create();
        init();
        setData();

        twitterLogin();

    }

    private void init(){
        etFirstName = (EditText) findViewById(R.id.edt_first_name);
        etLastName = (EditText) findViewById(R.id.edt_last_name);
        etEmail = (EditText) findViewById(R.id.edt_email);
        btnNext = (Button) findViewById(R.id.btn_next_page);
        btnLogout=(Button)findViewById(R.id.btn_log_out);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        facebookLoginbutton = (LoginButton) findViewById(R.id.facebook_login_button);
        googleLoginButton = (SignInButton) findViewById(R.id.google_login_button);
        linkedinLoginButton = (Button) findViewById(R.id.linkedin_login_button);
        toolbar=(TextView)findViewById(R.id.action);

    }
    private void setData(){
        btnLogout.setVisibility(View.GONE);
        toolbar.setText("Welcome");

        facebookLoginbutton.setOnClickListener(this);
        twitterLoginButton.setOnClickListener(this);
        googleLoginButton.setOnClickListener(this);
        linkedinLoginButton.setOnClickListener(this);
        btnNext.setOnClickListener(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,
                requestCode, resultCode, data);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        mGoogleApiClient.disconnect();
//    }


    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }


    private void twitterLogin() {

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        //firstName = result.data.name;
                        firstName = "";
                        lastName = "";
                        profilePic = result.data.profileImageUrl;
                        gender = "";
                        Log.v("TAG", firstName + "," + lastName + ",");
                        // etFirstName.setText(firstName);
                        userInformation = new UserInformation(firstName, lastName, email, profilePic, gender);
                        CommonData.saveUserInfo(MainActivity.this, userInformation);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.v("TAG TWITTER", "FAILURE");

                    }
                });


            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });


    }


    private void facebookLogin() {
        facebookLoginbutton.setReadPermissions(Arrays.asList("user_friends", "email", "public_profile"));

        facebookLoginbutton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("TAG", "SUCCESS");
                CommonProgress.showProgressDialog(MainActivity.this, null);


                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        Profile profile = Profile.getCurrentProfile();
                        Log.v("TAG FB",profile.getFirstName());

                        Log.v("TAG", jsonObject.toString());
                        try {
                            Log.v("TAG TRY","SUCCESS");
                            firstName = profile.getFirstName();
                            lastName = profile.getLastName();
                            email = jsonObject.getString("email");
                            gender = jsonObject.getString("gender");


                            etFirstName.setText(profile.getFirstName());
                            etFirstName.setEnabled(false);
                            etLastName.setText(profile.getLastName());
                            etLastName.setEnabled(false);
                            etEmail.setText(email);
                            etEmail.setEnabled(false);

                            profilePic = profile.getProfilePictureUri(150, 150) + "";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CommonProgress.dismissProgressDialog();
                        Log.v("TAG", firstName + "," + lastName + "," + email + "," + gender + profilePic);
                        userInformation = new UserInformation(firstName, lastName, email, profilePic, gender);
                        CommonData.saveUserInfo(MainActivity.this, userInformation);

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.v("TAG", "On Cancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.v("ERROR", error.toString());

            }
        });

    }

    private void googleLogin() {
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleLoginButton.setSize(SignInButton.SIZE_STANDARD);
        googleLoginButton.setScopes(gso.getScopeArray());
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }





    private void handleSignInResult(GoogleSignInResult result) {
        CommonProgress.showProgressDialog(MainActivity.this, null);
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
           // firstName = acct.getDisplayName();
            firstName="";
            lastName = "";
            email = acct.getEmail();
            //etFirstName.setText(firstName);

            etEmail.setText(email);
            etEmail.setEnabled(false);
            CommonProgress.dismissProgressDialog();
            gender="";
            profilePic = String.valueOf(acct.getPhotoUrl());
            Log.v("TAG", firstName + "," + lastName + "," + email + "," + gender + profilePic);
            userInformation = new UserInformation(firstName, lastName, email, profilePic, gender);
            CommonData.saveUserInfo(MainActivity.this, userInformation);
        }
    }


    public void login_linkedin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                CommonProgress.showProgressDialog(MainActivity.this,null);
                Log.v("TAG Linkedin", "SUCCESS");
                getUserData();
                CommonProgress.dismissProgressDialog();

                // Toast.makeText(getApplicationContext(), "success" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAuthError(LIAuthError error) {

                Log.v("TAG linkedin","FAILURE");
                Log.v("TAG linkedin",error.toString());
            }
        }, true);
    }

    // After complete authentication start new HomePage Activity


    // This method is used to make permissions to retrieve data from linkedin


    public void getUserData(){
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(MainActivity.this, Url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    Log.v("TAG LINKEDIN", "SUCCESS IN TRY");
                    getJsonData(result.getResponseDataAsJson());
                    //progress.dismiss();

                } catch (Exception e){
                    Log.v("TAG LINKEDIN","CATCH");
                    e.printStackTrace();
                }

            }

            @Override
            public void onApiError(LIApiError error) {
                Log.v("TAG API",error.toString());
                // ((TextView) findViewById(R.id.error)).setText(error.toString());

            }
        });
    }

    private void getJsonData(JSONObject responseDataAsJson) {

        try {
            email=responseDataAsJson.get("emailAddress").toString();
            firstName=responseDataAsJson.get("firstName").toString();
            lastName=responseDataAsJson.get("lastName").toString();
            profilePic=responseDataAsJson.get("pictureUrl").toString();

            etFirstName.setText(firstName);
            etLastName.setText(lastName);
            etEmail.setText(email);
            etFirstName.setEnabled(false);
            etLastName.setEnabled(false);
            etEmail.setEnabled(false);

            //gender=responseDataAsJson.get("phone-numbers").toString();
            Log.v("TAG Linkedin",firstName+","+lastName+","+email+","+profilePic);
            userInformation=new UserInformation(firstName, lastName, email, profilePic, gender);
            CommonData.saveUserInfo(MainActivity.this, userInformation);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Tag", "2" + e);
        }

    }
    private void getValue() {


        firstName = etFirstName.getText().toString();
        lastName = etLastName.getText().toString();
        email = etEmail.getText().toString();

        userInformation.setFirstName(firstName);
        userInformation.setLastName(lastName);
        userInformation.setEmail(email);




    }

    private boolean isValid() {


        if (firstName.isEmpty())

        {
            etFirstName.setError("enter valid first name");

            return false;
        }
        if (lastName.isEmpty())

        {
            etLastName.setError("enter last name");


            return false;
        }

        if (TextUtils.isEmpty(email) || !(email.contains("@"))) {
            etEmail.setError("enter valid email");
            return false;
        }
        return true;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_page:
                getValue();
                if (isValid()) {
                    Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, NextPageActivity.class);
                    intent.setAction(currentIntent);
                    startActivity(intent);
                    finish();

                }
                break;
            case R.id.facebook_login_button:
                currentIntent="FACEBOOK";
                facebookLogin();
                twitterLoginButton.setEnabled(false);
                googleLoginButton.setEnabled(false);
                linkedinLoginButton.setEnabled(false);
                break;
            case R.id.twitter_login_button:
                currentIntent="TWITTER";
                googleLoginButton.setEnabled(false);
                linkedinLoginButton.setEnabled(false);
                facebookLoginbutton.setEnabled(false);
                break;
            case R.id.google_login_button:
                currentIntent="GOOGLE";
                googleLogin();
                linkedinLoginButton.setEnabled(false);
                facebookLoginbutton.setEnabled(false);
                twitterLoginButton.setEnabled(false);
                break;
            case R.id.linkedin_login_button:
                currentIntent="LINKEDIN";
                login_linkedin();
                facebookLoginbutton.setEnabled(false);
                twitterLoginButton.setEnabled(false);
                googleLoginButton.setEnabled(false);
                break;


        }
    }



}




//@Override
//    protected void onPause() {
//        super.onPause();
//        AppEventsLogger.deactivateApp(this);
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        AppEventsLogger.activateApp(this);
//    }
//




//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        accessTokenTracker.stopTracking();
//        profileTracker.stopTracking();
//    }

//@Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            Log.d("TAG", "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        }
//        else {
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
//    }


