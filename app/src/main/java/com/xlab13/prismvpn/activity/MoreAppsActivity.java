package com.xlab13.prismvpn.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlab13.prismvpn.R;
import com.xlab13.prismvpn.adapter.AppsAdapter;
import static com.xlab13.prismvpn.activity.SplashActivity.apps;

public class MoreAppsActivity extends AppCompatActivity {
    Context context;

    private RecyclerView rvApps;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        context = this;

        rvApps = findViewById(R.id.rvApps);

        toolbar = (Toolbar) findViewById(R.id.appsToolbar);
        toolbar.setTitle("More Apps");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        rvApps.setLayoutManager(new LinearLayoutManager(context));
        rvApps.setAdapter(new AppsAdapter(context, apps));
    }
}
