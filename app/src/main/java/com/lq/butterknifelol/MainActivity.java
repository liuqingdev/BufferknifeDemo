package com.lq.butterknifelol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lq.animation.BindClick;
import com.lq.animation.BindView;
import com.lq.bufferknife.BufferKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(vules = R.id.butter_knife_tv)
    TextView tv;
    @BindView(vules = R.id.butter_knife_tv2)
    TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BufferKnife.bind(this);
        tv.setText("我用注解修改的");
    }

    @BindClick({R.id.butter_knife_tv,R.id.butter_knife_tv2})
    public void onClick(View view){
        Toast.makeText(this, "我被点击了", Toast.LENGTH_SHORT).show();
    }
}
