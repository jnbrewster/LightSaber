//https://play.google.com/store/apps/details?id=com.happydad.lightsaber.app

package com.happydad.LightSaber.app;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.happydad.LightSaber.app.ShakeDetector.OnShakeListener;

import java.util.Random;

public class MainActivity extends Activity {


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private MediaPlayer mHum;
    private Vibrator vib;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        saberStartSound();
        humStartSound();
        phoneScreenFadeIn();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(new OnShakeListener() {

            @Override
            public void onShake() {
                randomSwingSounds();
            }
        });

    }


    public void saberStartSound() {
        //Saber on sound
        final MediaPlayer onSaber = MediaPlayer.create(this, R.raw.on);
            onSaber.start();

        //Kill the saber on sound when it has played
        onSaber.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer onSaber) {
                onSaber.release();
            }
        });
    }


    public void humStartSound() {
        //Start and loop the saber hum sound
        mHum = MediaPlayer.create(this, R.raw.hum);
        mHum.setLooping(true);
        mHum.start();
    }


    public void randomSwingSounds() {
        int[] swingSounds = {
            R.raw.swing1, R.raw.swing2, R.raw.swing3, R.raw.swing4, R.raw.swing5, R.raw.swing6, R.raw.swing7, R.raw.hit1, R.raw.hit2
        };


        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(swingSounds.length);

        //Play random swing sounds
        MediaPlayer mp = MediaPlayer.create(MainActivity.this, swingSounds[randomNumber]);
        mp.start();

        //vibrate the phone when swinging
        //TODO figure out how to get vibrate to work only with swingSounds[7,8]
        phoneVibrate();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }


    public void phoneVibrate() {

        Vibrator vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        vib.vibrate(120);
    }


    public void phoneColorchange() {
        //TODO get the screen to light up or change color when the user moves the phone or gets a sound
        //When mp.isPlaying trigger the screen change
    }


    public void phoneScreenFadeIn() {
        //TODO get the screen to fade from black to the green screen on startup slower

        ImageView saberOverlay = (ImageView)findViewById(R.id.saberFadeIn);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        saberOverlay.startAnimation(fadeInAnimation);
    }


    @Override
    public void onResume() {
        super.onResume();
        //Start the sensors again on resume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        //kill the sensors if paused
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);

        //Stop the hum if paused
        mHum.release();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}