package org.shepherd.breathewell;

import com.google.android.glass.widget.CardScrollView;
import org.shepherd.breathewell.settings.GlassPreferenceActivity;
import org.shepherd.breathewell.settings.option.OptionsBuilder;
import org.shepherd.breathewell.settings.option.PreferenceOption;
import org.shepherd.breathewell.settings.types.AbstractPreference;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;

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
public class BreatheSettings extends GlassPreferenceActivity implements PhotoPreference.OnPhotoSelectedListener, MusicPreference.OnMusicSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent intent = new Intent(this, AlarmReceiver.class);
//        intent.putExtra("publish", true);
//        sendBroadcast(intent);
          createCards();
    }

    private void createCards() {


        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        addActivityPreference("test-key", "Instructional Video", InstructionalVideo.class);
        this.addTogglePreference("rate", "Rate Stress", true);
        this.addTogglePreference("play_music", "Background Music", true);
        //this.addTogglePreference("play_voice", "Voice guidance", true);
        String[] cycles = this.getResources().getStringArray(R.array.cycles);


        OptionsBuilder voiceOptions = new OptionsBuilder();
        voiceOptions.addOption("Female");
        voiceOptions.addOption("Male");
        voiceOptions.addOption("No voice");

        addChoicePreference("voiceoption", "Voice guidance", "Select your voice preference during your breathing session", voiceOptions.build());

        OptionsBuilder cycleTimes = new OptionsBuilder();
        for (String s: cycles)
        {
            cycleTimes.addOption(s);
        }


        addChoicePreference("cycles", "Breathe Cycles", "This setting controls how many cycles during a breathing session", cycleTimes.build());


        this.addPreference(getPhotoOptions());

        this.addPreference(getMusicOptions());

        // Builds all the preferences and shows them in a CardScrollView
        buildAndShowOptions();
    }

    public PhotoPreference getPhotoOptions() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int photoSelection = mPrefs.getInt("photo", 0);
        List<PreferenceOption> photos = new ArrayList<PreferenceOption>();
        PreferenceOption beach = new PreferenceOption("Beach", Integer.toString(R.drawable.bg_beach), R.drawable.bg_beach);
        PreferenceOption frozen = new PreferenceOption("Frozen Landscape",Integer.toString(R.drawable.bg_frozen), R.drawable.bg_frozen);
        PreferenceOption horse = new PreferenceOption("Horse in field", Integer.toString(R.drawable.bg_horse), R.drawable.bg_horse);
        PreferenceOption lake = new PreferenceOption("Lake", Integer.toString(R.drawable.bg_lake), R.drawable.bg_lake);
        PreferenceOption mountains = new PreferenceOption("Mountains", Integer.toString(R.drawable.bg_mountains), R.drawable.bg_mountains);
        PreferenceOption puppy = new PreferenceOption("Puppy & Kitten", Integer.toString(R.drawable.bg_puppy), R.drawable.bg_puppy);
        PreferenceOption trees = new PreferenceOption("Trees", Integer.toString(R.drawable.bg_trees), R.drawable.bg_trees);
        PreferenceOption water = new PreferenceOption("Water", Integer.toString(R.drawable.bg_water), R.drawable.bg_water);
        photos.add(beach);
        photos.add(frozen);
        photos.add(horse);
        photos.add(lake);
        photos.add(mountains);
        photos.add(puppy);
        photos.add(trees);
        photos.add(water);
        PreferenceOption photoStorage= null;
        if (photoSelection > 7) {
            String photoPath = mPrefs.getString("photopath", "");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
            photoStorage = new PreferenceOption("From Glass Storage", bitmap);
        }
        else
        {
            photoStorage = new PreferenceOption("From Glass Storage");
        }
        photos.add(photoStorage);



        PhotoPreference photoPref = new PhotoPreference(mPrefs,"photo", "Photos", "Select a photo", photos, this);
        return photoPref;

    }

    public MusicPreference getMusicOptions() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int photoSelection = mPrefs.getInt("music", 0);
        List<PreferenceOption> musicOptions = new ArrayList<PreferenceOption>();
        PreferenceOption m1 = new PreferenceOption("Fan");
        PreferenceOption m2 = new PreferenceOption("Meditate");
        PreferenceOption m3 = new PreferenceOption("Ocean Wave");
        PreferenceOption m4 = new PreferenceOption("Rain");
        PreferenceOption m5 = new PreferenceOption("Rainforest");
        PreferenceOption m6 = new PreferenceOption("Stream");
        PreferenceOption m7 = new PreferenceOption("Sunshine");

        musicOptions.add(m1);
        musicOptions.add(m2);
        musicOptions.add(m3);
        musicOptions.add(m4);
        musicOptions.add(m5);
        musicOptions.add(m6);
        musicOptions.add(m7);

        PreferenceOption musicStorage= new PreferenceOption("From Glass Storage");
        musicOptions.add(musicStorage);



        MusicPreference photoPref = new MusicPreference(mPrefs,"music", "Music", "Select music", musicOptions, this);
        return photoPref;

    }

    public void onPhotoSelected() {
        Intent intent=new Intent(this,FilePickerActivity.class);
        intent.putExtra("path", "Pictures/BreatheWell/");
        intent.putExtra("filetype", "Pictures");
        startActivityForResult(intent, 2);// Activity is started with requestCode 2
    }

    public void onMusicSelected() {
        Intent intent=new Intent(this,FilePickerActivity.class);
        intent.putExtra("path", "Pictures/BreatheWellMusic/");
        intent.putExtra("filetype", "Music");
        startActivityForResult(intent, 3);// Activity is started with requestCode 3
    }

    private void updatePhotoCard() {
        ArrayList<AbstractPreference> prefs = this.getPreferences();
        int cardNum = 0;
        for (AbstractPreference pref : prefs) {
            if (pref.getTitle().equals("Photos")) {
                PhotoPreference photoPref = getPhotoOptions();
                pref = photoPref;
                this.rebuildCard(cardNum, photoPref);
            }
            cardNum++;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(resultCode== RESULT_OK) {
            if (requestCode == 2) {
                String fileName = data.getStringExtra("file");
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString("photopath",fileName);
                edit.commit();
                updatePhotoCard();

            }
            if (requestCode == 3) {
                String fileName = data.getStringExtra("file");
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString("musicpath",fileName);
                edit.commit();
                updatePhotoCard();

            }
        }
    }

}
