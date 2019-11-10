package com.sanshy.unsubscribeforyoutube;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.model.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks

{

    public static String ChannelID;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    RewardedVideoAd r1,r2,r3,r4;
    InterstitialAd i1,i2,i3,i4,i5,i6,i7;

    long pageNumber = 1;
    long oldCount = 0;

    GoogleAccountCredential mCredential;
//    ProgressDialog mProgress;
//    ProgressDialog deleteWait;
    ChannelIdTask task;

    public TextView myChannelTitle,myChannelDes;
    public ImageView myChannelPic;
    public Button unsubscribeAllButton;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call YouTube Data API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBEPARTNER_CHANNEL_AUDIT, YouTubeScopes.YOUTUBEPARTNER, YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_FORCE_SSL, YouTubeScopes.YOUTUBE_UPLOAD};

    public ArrayList<SingleUnsubscribeList> SubList = new ArrayList<>();
    public ArrayList<String> CNameList = new ArrayList<>();
    String nextPageToken;
    String periviousPageToken;
    String titleString ="";
    String desString ="";
    String picUrl ="";
    long totalResult;
    ListView unListView;
    public UnListViewAdapter mListViewAdapter;

    long maxResultPerPage = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        r1 = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
        r1.setRewardedVideoAdListener(videoAdListener1());

        r2 = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
        r2.setRewardedVideoAdListener(videoAdListener2());

        r3 = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
        r3.setRewardedVideoAdListener(videoAdListener3());

        r4 = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
        r4.setRewardedVideoAdListener(videoAdListener4());


        i1 = new InterstitialAd(MainActivity.this);
        i1.setAdUnitId(getString(R.string.i1));
        i1.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());

        i2 = new InterstitialAd(MainActivity.this);
        i2.setAdUnitId(getString(R.string.i2));
        i2.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());

        i3 = new InterstitialAd(MainActivity.this);
        i3.setAdUnitId(getString(R.string.i3));
        i3.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());

        i4 = new InterstitialAd(MainActivity.this);
        i4.setAdUnitId(getString(R.string.i4));
        i4.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());

        i5 = new InterstitialAd(MainActivity.this);
        i5.setAdUnitId(getString(R.string.i5));
        i5.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());

        i6 = new InterstitialAd(MainActivity.this);
        i6.setAdUnitId(getString(R.string.i6));
        i6.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());

        i7 = new InterstitialAd(MainActivity.this);
        i7.setAdUnitId(getString(R.string.i7));
        i7.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());

        loadAds();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextCall();
//                Snackbar.make(view, nextPageToken, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pervCall();
//                Snackbar.make(view, periviousPageToken, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        myChannelTitle = hView.findViewById(R.id.my_channel_title);
        myChannelDes = hView.findViewById(R.id.my_channel_description);
        myChannelPic = hView.findViewById(R.id.my_channel_pic);

//        mProgress = new ProgressDialog(this);
//        mProgress.setMessage(getString(R.string.calling_youtube_api_dialog));
//        mProgress.setCancelable(false);
//
//        listWait = new ProgressDialog(this);
//        listWait.setMessage(getString(R.string.fatching_list_dialog));
//        listWait.setCancelable(false);
//
//        deleteWait = new ProgressDialog(this);
//        deleteWait.setMessage(getString(R.string.unsubscribe_process_dialog));

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


        unListView = findViewById(R.id.un_list_view);
        mListViewAdapter = new UnListViewAdapter(this, CNameList, SubList, new UnListViewAdapter.MyAdapterListener() {
            @Override
            public void unsubscriberListener(View v, int position) {

                showAd(false);

                ArrayList<String> myTempList = new ArrayList<>();
                myTempList.add(SubList.get(position).getSubscriptionId());
                new DeleteSub(mCredential,myTempList).execute();

            }
        });
        unListView.setAdapter(mListViewAdapter);
        unsubscribeAllButton = findViewById(R.id.unsubscribe_all);

        getResultsFromApi();

    }

    public RewardedVideoAdListener videoAdListener1(){
        return new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        };
    }
    public RewardedVideoAdListener videoAdListener2(){
        return new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        };
    }
    public RewardedVideoAdListener videoAdListener3(){
        return new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        };
    }
    public RewardedVideoAdListener videoAdListener4(){
        return new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        };
    }

    public void loadAds(){
        r1.loadAd(getString(R.string.r1),new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());
        r2.loadAd(getString(R.string.r2),new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());
        r3.loadAd(getString(R.string.r3),new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());
        r4.loadAd(getString(R.string.r4),new AdRequest.Builder().addTestDevice(getString(R.string.device_id)).build());
    }

    public void showAd(boolean reward){
        if (reward){
            if (r1.isLoaded()){
                r1.show();
                r1.loadAd(getString(R.string.r1),new AdRequest.Builder().build());
            }else if (r2.isLoaded()){
                r2.show();
                r2.loadAd(getString(R.string.r2),new AdRequest.Builder().build());

            }else if (r3.isLoaded()){
                r3.show();
                r3.loadAd(getString(R.string.r3),new AdRequest.Builder().build());
            }else if (r4.isLoaded()){
                r4.show();
                r4.loadAd(getString(R.string.r4),new AdRequest.Builder().build());
            }
            else{
                Toast.makeText(this, "Failed to Load Ad.", Toast.LENGTH_SHORT).show();
                loadAds();
            }
        }else {
            if (i1.isLoaded()){
                i1.show();
                i1.loadAd(new AdRequest.Builder().build());
            }else if (i2.isLoaded()){
                i2.show();
                i2.loadAd(new AdRequest.Builder().build());
            }else if (i3.isLoaded()){
                i3.show();
                i3.loadAd(new AdRequest.Builder().build());
            }else if (i4.isLoaded()){
                i4.show();
                i4.loadAd(new AdRequest.Builder().build());
            }else if (i5.isLoaded()){
                i5.show();
                i5.loadAd(new AdRequest.Builder().build());
            }else if (i6.isLoaded()){
                i6.show();
                i6.loadAd(new AdRequest.Builder().build());
            }else if (i7.isLoaded()){
                i7.show();
                i7.loadAd(new AdRequest.Builder().build());
            }else {
                Toast.makeText(this, "Failed to Load Ad", Toast.LENGTH_SHORT).show();
                i1.loadAd(new AdRequest.Builder().build());
                i2.loadAd(new AdRequest.Builder().build());
                i3.loadAd(new AdRequest.Builder().build());
                i4.loadAd(new AdRequest.Builder().build());
                i5.loadAd(new AdRequest.Builder().build());
                i6.loadAd(new AdRequest.Builder().build());
                i7.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    private void nextCall() {
        if (nextPageToken==null){
            Toast.makeText(MainActivity.this, "You Are on Last Page.", Toast.LENGTH_SHORT).show();
        }else {
            task = new ChannelIdTask(mCredential,true,false,false);
            task.execute();
        }
        showAd(false);
    }

    public void pervCall(){
        if (periviousPageToken==null){
            Toast.makeText(MainActivity.this, "You Are on First Page.", Toast.LENGTH_SHORT).show();
            task = new ChannelIdTask(mCredential,false,false,true);
        }
        else {
            task = new ChannelIdTask(mCredential,false,true,false);
            task.execute();
        }
        showAd(false);
    }

    public void ClearAllSubscriberFromChannel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(R.string.sure_text)
                .setMessage(getString(R.string.clear_all_warning_))
                .setPositiveButton(getString(R.string.sure_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteSubAll(mCredential).execute();
                    }
                })
                .setNeutralButton(getString(R.string.this_page_text_), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        allUnsubscribe();
                    }
                });

        builder.create().show();
    }

    public void allUnsubscribe(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

        builder1.setTitle(getString(R.string.reward_video_dialog_title))
                .setMessage(getString(R.string.reward_video_request))
                .setPositiveButton(getString(R.string.ok_bt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAd(true);
                        ArrayList<String> tempList = new ArrayList<>();
                        for (int q = 0; q < SubList.size(); q ++){
                            tempList.add(SubList.get(q).getSubscriptionId());
                        }
                        new DeleteSub(mCredential,tempList).execute();
                        pervCall();
                    }
                });

        builder1.create().show();
    }

    public void allUnsubscribeButton(View view){
        db.child(getString(R.string.donated_list_key)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    ArrayList<String> first = new ArrayList<>();
                    first.add(ChannelID);
                    db.child(getString(R.string.donated_list_key)).setValue(first);
                }else {
                    try{
                        ArrayList<String> donarIdList = (ArrayList<String>) dataSnapshot.getValue();
                        if (donarIdList.contains(ChannelID)){
                            ClearAllSubscriberFromChannel();
                        }else {
                            allUnsubscribe();
                        }
                    }catch (NullPointerException ne){
                        if (ChannelID==null){
                            myDialog(getString(R.string.restart_request));
                        }
                    }
                    catch (Exception ex){

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void myDialog(String SMS){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(SMS)
                .setPositiveButton("OK",null);

        builder.create().show();
    }
    public void myDialog(String SMS, final boolean callBack){


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(SMS)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callBack){
                            getResultsFromApi();
                        }
                    }
                });

        builder.create().show();
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        i7.loadAd(new AdRequest.Builder().build());
        i6.loadAd(new AdRequest.Builder().build());
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            myDialog("No network connection available.",true);

        } else {
            task = new ChannelIdTask(mCredential,false,false,true);
            task.execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    myDialog("This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    //TODO /**** Channel Id Getting Async Task ****/

    private class MakeRequestTask extends AsyncTask<Void, Void, String> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private String getDataFromApi() throws IOException {

            YouTube youtube = mService;
            try {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("part", "snippet,contentDetails,statistics");
                parameters.put("mine", "true");

                YouTube.Channels.List channelsListMineRequest = youtube.channels().list(parameters.get("part").toString());
                if (parameters.containsKey("mine") && parameters.get("mine") != "") {
                    boolean mine = (parameters.get("mine") == "true") ? true : false;
                    channelsListMineRequest.setMine(mine);
                }

                ChannelListResponse response = channelsListMineRequest.execute();
                System.out.println(response);
                return response.getItems().get(0).getId();
            }catch (Exception ex){
                return null;
            }
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String output) {
            if (output == null) {
                myDialog("No results returned.");
            } else {
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.channel_id),output);
                editor.apply();
                ChannelID = output;
            }
        }

        @Override
        protected void onCancelled() {
//            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    myDialog("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                myDialog("Request cancelled.");
            }
        }
    }

    private class ChannelIdTask extends AsyncTask<Void, Void, ArrayList<SingleUnsubscribeList>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;
        boolean nxtPage;
        boolean prvsPage;
        boolean mainPage;

        ChannelIdTask(GoogleAccountCredential credential,boolean nxtPage, boolean prvsPage, boolean mainPage) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();

            this.nxtPage = nxtPage;
            this.prvsPage = prvsPage;
            this.mainPage = mainPage;
        }

        @Override
        protected ArrayList<SingleUnsubscribeList> doInBackground(Void... ChannelId) {
            try {
                if (prvsPage){
                    pageNumber--;
                    return getDataFromApiPage(periviousPageToken);
                }
                else if (nxtPage){
                    pageNumber++;
                    return getDataFromApiPage(nextPageToken);
                }
                else {
                    return getDataFromApi();
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(false);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private ArrayList<SingleUnsubscribeList> getDataFromApi() throws IOException {
            System.out.println("Correct");
            YouTube youtube = mService;
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("part", "snippet,contentDetails");
                parameters.put("mine", "true");

                YouTube.Subscriptions.List subscriptionsListMySubscriptionsRequest = youtube.subscriptions().list(parameters.get("part").toString());
                if (parameters.containsKey("mine") && parameters.get("mine") != "") {
                    boolean mine = (parameters.get("mine") == "true") ? true : false;
                    subscriptionsListMySubscriptionsRequest.setMine(mine);
                }
                subscriptionsListMySubscriptionsRequest.setMaxResults(maxResultPerPage);

                SubscriptionListResponse response = subscriptionsListMySubscriptionsRequest.execute();

                nextPageToken = response.getNextPageToken();
                periviousPageToken = response.getPrevPageToken();
                totalResult = response.getPageInfo().getTotalResults();

                List<Subscription> myList = response.getItems();

                for (int i = 0; i < myList.size(); i++){
                    String PhotoURL = response.getItems().get(i).getSnippet().getThumbnails().getDefault().getUrl();
                    String ChannelName = response.getItems().get(i).getSnippet().getTitle();
                    long videosCount = response.getItems().get(i).getContentDetails().getTotalItemCount();
                    String subChannelId = response.getItems().get(i).getId();

                    SingleUnsubscribeList varb = new SingleUnsubscribeList(PhotoURL,ChannelName,videosCount,0,subChannelId);
                    SubList.add(varb);
                    CNameList.add(ChannelName);
                }


                System.out.println(response);


                HashMap<String, String> parameters2 = new HashMap<>();
                parameters2.put("part", "snippet,contentDetails,statistics");
                parameters2.put("mine", "true");

                YouTube.Channels.List channelsListMineRequest = youtube.channels().list(parameters2.get("part").toString());
                if (parameters2.containsKey("mine") && parameters2.get("mine") != "") {
                    boolean mine = (parameters2.get("mine") == "true") ? true : false;
                    channelsListMineRequest.setMine(mine);
                }

                ChannelListResponse response2 = channelsListMineRequest.execute();
                titleString = response2.getItems().get(0).getSnippet().getTitle();
                desString = response2.getItems().get(0).getSnippet().getDescription();
                picUrl = response2.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();
                System.out.println(response2);

                pageNumber = 1;
                return SubList;

        }
        private ArrayList<SingleUnsubscribeList> getDataFromApiPage(String pageToken) throws IOException {
            YouTube youtube = mService;
            try {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("pageToken", pageToken);
                parameters.put("part", "snippet,contentDetails");
                parameters.put("mine", "true");

                YouTube.Subscriptions.List subscriptionsListMySubscriptionsRequest = youtube.subscriptions().list(parameters.get("part").toString());
                if (parameters.containsKey("mine") && parameters.get("mine") != "") {
                    boolean mine = (parameters.get("mine") == "true") ? true : false;
                    subscriptionsListMySubscriptionsRequest.setMine(mine);
                }
                subscriptionsListMySubscriptionsRequest.setMaxResults(maxResultPerPage);
                if (!pageToken.isEmpty()){
                    subscriptionsListMySubscriptionsRequest.setPageToken(pageToken);
                }

                SubscriptionListResponse response = subscriptionsListMySubscriptionsRequest.execute();

                nextPageToken = response.getNextPageToken();
                periviousPageToken = response.getPrevPageToken();

                List<Subscription> myList = response.getItems();

                System.out.println(response.getPageInfo().getTotalResults());

                for (int i = 0; i < myList.size(); i++){
                    String PhotoURL = response.getItems().get(i).getSnippet().getThumbnails().getDefault().getUrl();
                    String ChannelName = response.getItems().get(i).getSnippet().getTitle();
                    long videosCount = response.getItems().get(i).getContentDetails().getTotalItemCount();
                    String subChannelId = response.getItems().get(i).getId();


                    SingleUnsubscribeList varb = new SingleUnsubscribeList(PhotoURL,ChannelName,videosCount,0,subChannelId);
                    SubList.add(varb);
                    CNameList.add(ChannelName);
                }

                System.out.println(response);

                return SubList;
            }catch (Exception ex){
                return null;
            }
        }


        @Override
        protected void onPreExecute() {
            SubList.clear();
            CNameList.clear();
//            listWait.show();
        }

        @Override
        protected void onPostExecute(ArrayList<SingleUnsubscribeList> output) {
//            listWait.hide();
            if (output == null) {
                myDialog(getString(R.string.no_list_found));
            } else {
                SubList = output;
                mListViewAdapter.notifyDataSetChanged();
                myChannelTitle.setText(titleString);
                try{
                    myChannelDes.setText(desString.subSequence(0,20)+"...");
                }catch (Exception ex){Log.d("Description ",ex.toString());}
                Glide.with(MainActivity.this)
                        .load(picUrl)
                        .into(myChannelPic);

                try {
                    long CurrentCount = 0;
                    if (prvsPage){
                        oldCount = oldCount-SubList.size();
                        CurrentCount = oldCount-SubList.size();
                    }else {
                        CurrentCount = oldCount;
                        oldCount = oldCount+SubList.size();

                    }

                    unsubscribeAllButton.setText(getString(R.string.unsubscribe_all_button)+CurrentCount+"-"+oldCount+"/"+totalResult);
                }catch (Exception ex){}

                Toast.makeText(MainActivity.this, getString(R.string.page_number_toast)+pageNumber, Toast.LENGTH_SHORT).show();
                new MakeRequestTask(mCredential).execute();
            }
        }

        @Override
        protected void onCancelled() {
//            listWait.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    myDialog("The following error occurred 222:\n"
                            + mLastError.getMessage());
                }
            } else {
                myDialog("Request cancelled.");
            }
        }
    }

    private class DeleteSub extends AsyncTask<Void, Void, ArrayList<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;
        ArrayList<String> SubscriptionIdDeleteSub = new ArrayList<>();

        DeleteSub(GoogleAccountCredential credential,ArrayList<String> SubscriptionIdDeleteSub) {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();

            this.SubscriptionIdDeleteSub = SubscriptionIdDeleteSub;

        }

        @Override
        protected ArrayList<String> doInBackground(Void... ChannelId) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(false);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private ArrayList<String> getDataFromApi() throws IOException {
            YouTube youtube = mService;

            for (int p = 0; p < SubscriptionIdDeleteSub.size() ; p++){
                YouTube.Subscriptions.Delete subscriptionsDeleteRequest = youtube.subscriptions().delete(SubscriptionIdDeleteSub.get(p));
                subscriptionsDeleteRequest.execute();
            }

            return SubscriptionIdDeleteSub;

        }

        @Override
        protected void onPreExecute() {
//            deleteWait.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> output) {
//            deleteWait.hide();
            if (output == null) {
                myDialog("No List Found.");
            } else {

                for (int l = 0; l < SubList.size();l++){
                    if (output.contains(SubList.get(l).getSubscriptionId()))
                    {
                        SubList.remove(l);
                        CNameList.remove(l);
                    }
                }
                long CurrentCount = 0;
                if (!(SubList.size()==0)){
                    oldCount = oldCount-output.size();
                    CurrentCount = oldCount-SubList.size();
                }
                totalResult = totalResult - output.size();

                final long size = output.size();
                db.child(getString(R.string.database_unsubscribe_key)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long temp = 0;
                        try{
                         temp = (long) dataSnapshot.getValue();
                        }catch (Exception ex){}

                        db.child(getString(R.string.database_unsubscribe_key)).setValue(size+temp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                unsubscribeAllButton.setText(getString(R.string.unsubscribe_all_button)+CurrentCount+"-"+oldCount+"/"+totalResult);
                mListViewAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
//            deleteWait.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    myDialog("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                myDialog("Request cancelled.");
            }
        }
    }

    private class DeleteSubAll extends AsyncTask<Void, Void, ArrayList<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;
        ArrayList<String> SubscriptionIdDeleteSub = new ArrayList<>();

        DeleteSubAll(GoogleAccountCredential credential) {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... ChannelId) {
            try {
                getDataFromApi();
                return getDeleteDone();
            } catch (Exception e) {
                mLastError = e;
                cancel(false);
                return null;
            }
        }

        public String NextPageTokenAllClear;

        private void getDataFromApi() throws IOException {
            YouTube youtube = mService;
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("pageToken", NextPageTokenAllClear);
                parameters.put("part", "snippet,contentDetails");
                parameters.put("mine", "true");

                YouTube.Subscriptions.List subscriptionsListMySubscriptionsRequest = youtube.subscriptions().list(parameters.get("part").toString());
                if (parameters.containsKey("mine") && parameters.get("mine") != "") {
                    boolean mine = (parameters.get("mine") == "true") ? true : false;
                    subscriptionsListMySubscriptionsRequest.setMine(mine);
                }
                subscriptionsListMySubscriptionsRequest.setMaxResults((long) 50);
                if (NextPageTokenAllClear!=null) {
                    subscriptionsListMySubscriptionsRequest.setPageToken(NextPageTokenAllClear);
                }

                SubscriptionListResponse response = subscriptionsListMySubscriptionsRequest.execute();

                nextPageToken = response.getNextPageToken();
                List<Subscription> myList = response.getItems();

                for (int i = 0; i < myList.size(); i++) {
                     String subId = response.getItems().get(i).getId();
                     SubscriptionIdDeleteSub.add(subId);
                }


                if (response.getNextPageToken()!=null){
                    NextPageTokenAllClear = response.getNextPageToken();
                    getDataFromApi();
                }

            }
        private ArrayList<String> getDeleteDone() throws IOException {
            YouTube youtube = mService;

            for (int p = 0; p < SubscriptionIdDeleteSub.size() ; p++){
                YouTube.Subscriptions.Delete subscriptionsDeleteRequest = youtube.subscriptions().delete(SubscriptionIdDeleteSub.get(p));
                subscriptionsDeleteRequest.execute();
            }

            return SubscriptionIdDeleteSub;

        }

        @Override
        protected void onPreExecute() {
            SubscriptionIdDeleteSub.clear();
//            deleteWait.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> output) {
//            deleteWait.hide();
            if (output == null) {
                myDialog(getString(R.string.no_list_found));
            } else {

                myDialog(getString(R.string.unsubscribe_done));

                SubList.clear();
                CNameList.clear();
                long CurrentCount = 0;
                oldCount = 0;
                totalResult = 0;

                final long size = output.size();
                db.child(getString(R.string.database_unsubscribe_key)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long temp = 0;
                        try{
                            temp = (long) dataSnapshot.getValue();
                        }catch (Exception ex){}

                        db.child(getString(R.string.database_unsubscribe_key)).setValue(size+temp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                unsubscribeAllButton.setText(getString(R.string.unsubscribe_all_button)+CurrentCount+"-"+oldCount+"/"+totalResult);
                mListViewAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
//            deleteWait.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    myDialog("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                myDialog("Request cancelled.");
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            clearData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_unsubscribe) {
            getResultsFromApi();
        } else if (id == R.id.nav_reset) {

            clearData();

        } else if (id == R.id.nav_coming_soon) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(getString(R.string.send_feedback))
                    .setMessage(getString(R.string.feedback_request)+
                    getString(R.string.whats_app_contact)+"\n"+
                    getString(R.string.instagram_id))
                    .setPositiveButton(getString(R.string.send_feedback), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto: sbswami24@gmail.com"));
                            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                        }
                    })
                    .setNegativeButton(getString(R.string.close_bt),null);

            builder.create().show();
        } else if (id == R.id.nav_share) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share_text) +
                    getString(R.string.download_now) +
                    getString(R.string.app_link));
            startActivity(Intent.createChooser(intent,getString(R.string.share_via)));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void clearData(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(getString(R.string.reset_title))
                .setMessage(getString(R.string.reset_msg_))
                .setPositiveButton(getString(R.string.ok_bt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            ((ActivityManager)MainActivity.this.getSystemService(ACTIVITY_SERVICE))
                                    .clearApplicationUserData();
                        }catch (Exception ex){}
                    }
                }).setNegativeButton(getString(R.string.close_bt),null);

        builder.create().show();


    }

}
