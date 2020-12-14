package com.xlab13.prismvpn.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.AccelerateInterpolator;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;

import at.grabner.circleprogress.BuildConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.xlab13.prismvpn.R;

import com.xlab13.prismvpn.adapter.AppsAdapter;
import com.xlab13.prismvpn.api.AppItem;
import com.xlab13.prismvpn.api.AppsApi;
import com.xlab13.prismvpn.api.AppsResponse;
import com.xlab13.prismvpn.model.Server;
import com.xlab13.prismvpn.util.PropertiesService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    DecoView arcView, arcView2;
    public static final String EXTRA_COUNTRY = "country";
    private PopupWindow popupWindow;
    private RelativeLayout homeContextRL;
    private AdView adView;
    TextView centree;
    private List<Server> countryList;


    CardView mCardViewShare;
    Intent i;

    private String TAG = this.getClass().getSimpleName();

    private TextView textView;

   // private ConsentForm form;

    private BillingClient mBillingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();

    private String mSkuId = "donate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeContextRL = (RelativeLayout) findViewById(R.id.homeContextRL);
        countryList = dbHelper.getUniqueCountries();

        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
        initNavigationView();
       // checkForConsent();

        adView = findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);




        final InterstitialAd mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit));
        mInterstitial.loadAd(new AdRequest.Builder().build());

        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // TODO Auto-generated method stub
                super.onAdLoaded();
                if (mInterstitial.isLoaded()) {
                    mInterstitial.show();
                }
            }
        });

        if (BaseActivity.connectedServer == null) {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("No VPN Connected");
            hello.setBackgroundResource(R.drawable.button2);
        }
        else {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("Connected");
            hello.setBackgroundResource(R.drawable.button3);
        }

        centree = (TextView) findViewById(R.id.centree);
        arcView = (DecoView) findViewById(R.id.dynamicArcView2);
        arcView2 = (DecoView) findViewById(R.id.dynamicArcView3);

        long totalServ = dbHelper.getCount();
       // checkshowint();
        String totalServers = String.format(getResources().getString(R.string.total_servers), totalServ);
        centree.setText(totalServers);

        arcView2.setVisibility(View.VISIBLE);
        arcView.setVisibility(View.GONE);

        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 0)
                .setInterpolator(new AccelerateInterpolator())
                .build());

        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00000000"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#ffffff"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        int series1Index2 = arcView.addSeries(seriesItem2);
        Random ran2 = new Random();
        int proc = ran2.nextInt(10) + 5;
        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(600)
                .build());


        arcView.addEvent(new DecoEvent.Builder(proc)
                .setIndex(series1Index2).setDelay(2000)
                .setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {
                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {
                        long totalServ = dbHelper.getCount();

                        String totalServers = String.format(getResources().getString(R.string.total_servers), totalServ);
                        centree.setText(totalServers);
                    }
        }).build());






        mCardViewShare = (CardView) findViewById(R.id.CardViewShare);

        mCardViewShare.setOnClickListener(v -> startFragmentActivity(FragmentWrapperActivity.BATTERY_SAVER_CODE));

        CardView button1 = (CardView) findViewById(R.id.homeBtnRandomConnection);
        button1.setOnClickListener(v -> {
            //TODO CLICK HERE START
            sendTouchButton("homeBtnRandomConnection");
            Server randomServer = getRandomServer();
            if (randomServer != null) {
                newConnecting(randomServer, true, true);
            } else {
                String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                Toast.makeText(MainActivity.this, randomError, Toast.LENGTH_LONG).show();
            }
        });

        CardView button2 = (CardView) findViewById(R.id.homeBtnChooseCountry);
        button2.setOnClickListener(v -> {
            sendTouchButton("homeBtnChooseCountry");
            chooseCountry();

        });


        CardView button = (CardView) findViewById(R.id.button);
        button.setOnClickListener(v -> startFragmentActivity(FragmentWrapperActivity.BOOSTER_CODE));

        ////// donate button
        initBilling();
        CardView buttonDonate = (CardView) findViewById(R.id.buttonDonate);
        buttonDonate.setOnClickListener(v -> launchBilling(mSkuId));

        countLaunch();
    }

    private void startFragmentActivity (String REQUEST_ACTIVITY_CODE) {
        Intent intent = new Intent (getApplicationContext(), FragmentWrapperActivity.class);
        intent.putExtra(FragmentWrapperActivity.REQUEST_ACTIVITY_CODE, REQUEST_ACTIVITY_CODE);
        startActivity (intent);
    }

    private void startFragmentActivity (String REQUEST_ACTIVITY_CODE, String RUNTIME_MODE) {
        Intent intent = new Intent (getApplicationContext(), FragmentWrapperActivity.class);
        intent.putExtra(FragmentWrapperActivity.REQUEST_ACTIVITY_CODE, REQUEST_ACTIVITY_CODE);
        intent.putExtra(FragmentWrapperActivity.RUNTIME_MODE, RUNTIME_MODE);
        startActivity (intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        if (BaseActivity.connectedServer == null) {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("No VPN Connected");
        }
        else {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("Connected");
            hello.setBackgroundResource(R.drawable.button3);
        }

        invalidateOptionsMenu();


    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }



    @Override
    protected boolean useHomeButton() {
        return true;
    }

    public void homeOnClick(View view) {
        switch (view.getId()) {
            case R.id.homeBtnChooseCountry:
                sendTouchButton("homeBtnChooseCountry");
                chooseCountry();
                break;
            case R.id.homeBtnRandomConnection:
                sendTouchButton("homeBtnRandomConnection");
                Server randomServer = getRandomServer();
                if (randomServer != null) {
                    newConnecting(randomServer, true, true);
                } else {
                    String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                    Toast.makeText(this, randomError, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private void chooseCountry() {
        View view = initPopUp(R.layout.choose_country, 0.6f, 0.8f, 0.8f, 0.7f);

        final List<String> countryListName = new ArrayList<String>();
        for (Server server : countryList) {
            String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                    localeCountries.get(server.getCountryShort()) : server.getCountryLong();
            countryListName.add(localeCountryName);
        }

        ListView lvCountry = (ListView) view.findViewById(R.id.homeCountryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryListName);

        lvCountry.setAdapter(adapter);
        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                onSelectCountry(countryList.get(position));
            }
        });

        popupWindow.showAtLocation(homeContextRL, Gravity.CENTER,0, 0);
    }

    private View initPopUp(int resourse,
                            float landPercentW,
                            float landPercentH,
                            float portraitPercentW,
                            float portraitPercentH) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resourse, null);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * landPercentW),
                    (int)(heightWindow * landPercentH)
            );
        } else {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * portraitPercentW),
                    (int)(heightWindow * portraitPercentH)
            );
        }


        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        return view;
    }

    private void onSelectCountry(Server server) {
        Intent intent = new Intent(getApplicationContext(), VPNListActivity.class);
        intent.putExtra(EXTRA_COUNTRY, server.getCountryShort());
        startActivity(intent);
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    private void initNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_speedtest) {
            startActivity(new Intent(this, SpeedTestActivity.class));


        }  else if (id == R.id.nav_home){
            startActivity(new Intent(this, MainActivity.class));
        }  else if (id == R.id.nav_vpnlist){
        }
        else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Best Free Vpn app download now. https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share App");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }else if (id == R.id.rate_us) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
        }
        else if (id == R.id.about_me) {
            aboutMyApp();

        }

        else if (id == R.id.nav_speedbooster) {
            startFragmentActivity(FragmentWrapperActivity.BOOSTER_CODE);

        }
        else if (id == R.id.nav_batterysaver) {
            startFragmentActivity(FragmentWrapperActivity.BATTERY_SAVER_CODE);

        }

        else if (id == R.id.privacypolicy) {
            startActivity(new Intent(MainActivity.this, TOSActivity.class));

        }

        else if (id == R.id.moreapp) {
            startActivity(new Intent(this, MoreAppsActivity.class));
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void aboutMyApp() {

        MaterialDialog.Builder bulder = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .customView(R.layout.about, true)
                .backgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .titleColorRes(android.R.color.white)
                .positiveText("MORE APPS")
                .positiveColor(getResources().getColor(android.R.color.white))
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .limitIconToDefaultSize()
                .onPositive((dialog, which) -> {
                    Uri uri = Uri.parse("market://search?q=pub:" + "PA Production");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/search?q=pub:" + "PA Production")));
                    }
                });

        MaterialDialog materialDialog = bulder.build();

        TextView versionCode = (TextView) materialDialog.findViewById(R.id.version_code);
        TextView versionName = (TextView) materialDialog.findViewById(R.id.version_name);
        versionCode.setText(String.valueOf("Version Code : " + BuildConfig.VERSION_CODE));
        versionName.setText(String.valueOf("Version Name : " + BuildConfig.VERSION_NAME));

        materialDialog.show();
    }

    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }
        });
        toggle.syncState();
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        return toolbar;
    }




    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    ///////////////////// donate section

    private void initBilling() {
        Log.i("===","try init payment service");
        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                    payComplete();
                }
            }
        }).build();
        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                int billingResponseCode = billingResult.getResponseCode();
                Log.i("===","payment service fail , code : "+ billingResponseCode);
                if (billingResponseCode == BillingClient.BillingResponseCode.OK) {
                    //below you can query information about products and purchase
                    Log.i("===","payment service ok");
                    querySkuDetails(); //query for products
                    List<Purchase> purchasesList = queryPurchases(); //query for purchases

                    //if the purchase has already been made to give the goods
                    for (int i = 0; i < purchasesList.size(); i++) {
                        String purchaseId = purchasesList.get(i).getSku();
                        if(TextUtils.equals(mSkuId, purchaseId)) {
                            payComplete();
                        }
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                //here when something went wrong, e.g. no internet connection
                Log.i("===","payment service fail");
            }
        });
    }

    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(mSkuId);
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == 0) {
                    for (SkuDetails skuDetails : list) {
                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                        Log.i("===",skuDetails.getDescription());
                    }
                }
            }
        });
    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        return purchasesResult.getPurchasesList();
    }

    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(skuId))
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void payComplete() {
        Toast.makeText(this, "Thanks for your donate! We will make the app better :)", Toast.LENGTH_SHORT).show();
    }

    private void countLaunch(){
        SharedPreferences sPref = getSharedPreferences("app", Context.MODE_PRIVATE);
        int launches = sPref.getInt("countLaunch", 0);
        launches++;
        if (launches >= 10){
            launches = 0;
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this)
                    .setMessage(R.string.more_apps)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), MoreAppsActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            Dialog dialog = mBuilder.create();
            dialog.show();
        }
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("countLaunch", launches);
        ed.commit();
    }
}
