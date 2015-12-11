package org.shepherd.breathewell;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.shepherd.breathewell.adapter.CardAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private CardScrollView mCardScroller;

    private static final String LOG_TAG = MainActivity.class.getName();

    private CardScrollAdapter mAdapter;



    static final int BREATHE = 0;
    static final int SETUP_REMINDERS = 1;
    static final int BREATHE_SETTINGS = 2;
    static final int SETTINGS = 3;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();
        mCardScroller.setHorizontalScrollBarEnabled(true);

        setContentView(mCardScroller);
    }

    /**
     * Different type of activities can be shown, when tapped on a card.
     */
    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Clicked view at position " + position + ", row-id " + id);
                int soundEffect = Sounds.TAP;
                boolean bPlaySound = true;
                switch (position) {
                    case BREATHE:
                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        boolean rate = mPrefs.getBoolean("rate", true);
                        if (rate) {
                            startActivity(new Intent(MainActivity.this, RateActivity.class));
                        } else
                        {
                            startActivity(new Intent(MainActivity.this, BreatheActivity.class));
                        }
                        break;

                    case SETUP_REMINDERS:
                        ((App) MainActivity.this.getApplication()).trackView("setupreminders");
                        startActivity(new Intent(MainActivity.this, ReminderActivity.class));
                        break;

                    case BREATHE_SETTINGS:
                        ((App) MainActivity.this.getApplication()).trackView("breathesettings");
                        startActivity(new Intent(MainActivity.this, BreatheSettings.class));
                        break;

                    case SETTINGS:
                        ((App) MainActivity.this.getApplication()).trackView("settings");
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;


                    default:
                        soundEffect = Sounds.ERROR;
                        Log.d(LOG_TAG, "Don't show anything");
                }

                // Play sound.
                if (bPlaySound) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(soundEffect);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();


        CardBuilder cbBreathe = new CardBuilder(this, CardBuilder.Layout.MENU);
        cbBreathe.setIcon(R.drawable.breathe);
        cbBreathe.setText(R.string.start_breathing);
        cards.add(cbBreathe);

        CardBuilder cbSetupReminders = new CardBuilder(this, CardBuilder.Layout.MENU);
        cbSetupReminders.setIcon(R.drawable.ic_action_alarms);
        cbSetupReminders.setText(R.string.setup_reminders);
        cards.add(cbSetupReminders);

        CardBuilder cbBreatheSettings = new CardBuilder(this, CardBuilder.Layout.MENU);
        cbBreatheSettings.setIcon(R.drawable.ic_action_import_export);
        cbBreatheSettings.setText(R.string.breathe_settings);


//        CardBuilder cbSettings = new CardBuilder(this, CardBuilder.Layout.MENU);
//        cbSettings.setIcon(R.drawable.ic_action_settings);
//        cbSettings.setText(R.string.other_settings);


        PackageInfo pinfo;
        String footerNote = "";
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            footerNote = "Version " + pinfo.versionName + " build " + pinfo.versionCode;

            String androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            footerNote += "\nDevice Id: " + androidID;
            //ET2.setText(versionNumber);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
        }
        cbBreatheSettings.setFootnote(footerNote);
        //cards.add(cbSettings);
        cards.add(cbBreatheSettings);

        return cards;
    }

}
