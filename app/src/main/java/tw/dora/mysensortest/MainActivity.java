package tw.dora.mysensortest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private MyListener myListener;
    private Sensor sensor;
    private TextView x, y, z;

    private SoundPool sp;
    private int s1, s2;

    private ImageView img;
    private File sdroot,picfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  ||
                ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);

        }else{
            init();
        }


    }

    private void init() {

        sdroot = Environment.getExternalStorageDirectory();
        picfile = new File(sdroot.getAbsolutePath(),"brad.png");

        img = findViewById(R.id.img);

        sp = new SoundPool(2,AudioManager.STREAM_MUSIC,0);
        sp.load(this,R.raw.s1,1);
        sp.load(this,R.raw.s2,2);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //以下列出手機所有感應器名稱及種類
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor:deviceSensors){
            String type = sensor.getStringType();
            String name = sensor.getName();
            Log.v("brad",name+": "+type);
        }

        //sensor = mSensorManager.getDefaultSensor(sensor.TYPE_ACCELEROMETER);
        //sensor = mSensorManager.getDefaultSensor(sensor.TYPE_LIGHT);
        //sensor = mSensorManager.getDefaultSensor(sensor.TYPE_PRESSURE);
        //sensor = mSensorManager.getDefaultSensor(sensor.TYPE_MAGNETIC_FIELD);
        sensor = mSensorManager.getDefaultSensor(sensor.TYPE_ROTATION_VECTOR);

        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        z = findViewById(R.id.z);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myListener = new MyListener();
        mSensorManager.registerListener(myListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(myListener);
    }

    public void test1(View view) {
        sp.play(s1,0.5f,0.5f,1,0,1);
    }

    public void test2(View view) {
        sp.play(s2,0.5f,0.5f,1,0,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    afterCamera1(data);
                    break;
                case 2:
                    afterCamera2();
                    break;

            }
        }

    }

    private void afterCamera1(Intent data){
        Bundle bundle = data.getExtras();
        Bitmap bmp = (Bitmap) bundle.get("data");
        img.setImageBitmap(bmp);
    }

    private void afterCamera2(){
        Bitmap bmp = BitmapFactory.decodeFile(picfile.getAbsolutePath());
        img.setImageBitmap(bmp);
    }

    public void test3(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,1);

    }

    public void test4(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(picfile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent,2);

    }

    private class MyListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] v = event.values; // 任何感應器最多三個值[3]
            x.setText("X: "+(int)(v[0]*10)/10f);
            if(v.length>=2) y.setText("Y: "+(int)(v[1]*10)/10f);
            if(v.length>=3) z.setText("Z: "+(int)(v[2]*10)/10f);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}
