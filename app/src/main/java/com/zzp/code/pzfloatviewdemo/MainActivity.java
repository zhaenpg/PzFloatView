package com.zzp.code.pzfloatviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zzp.code.pzfloatview.OnPzFloatViewClickListener;
import com.zzp.code.pzfloatview.PzFloatView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PzFloatView pzFloatView = new PzFloatView(MainActivity.this,0,0,null);
        pzFloatView.setCanHideInEdge(false)
                .setCanScrollToLeft(true)
                .setCanScrollToBottom(true)
                .setCanScrollToTop(true)
                .setCanScrollToRight(true)
                .setFloatViewImageRes(R.mipmap.ic_launcher)
                .setUsePermissionVersion(false)
                .setClickListener(new OnPzFloatViewClickListener() {
                    @Override
                    public void onFloatViewClick() {
                        Toast.makeText(MainActivity.this, "click float view", Toast.LENGTH_SHORT).show();
                    }
                })
                .addToWindow();
        pzFloatView.show();
    }
}
