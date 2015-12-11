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
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.shepherd.breathewell.adapter.CardAdapter;
import org.shepherd.breathewell.adapter.ConfirmAdapter;

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
public class ReminderActivity extends Activity {

    private List<CardBuilder> mCards;
    private CardScrollView mCardScroller;
    private static final int SPEECH_REQUEST_1 = 1;
    private static final int SPEECH_REQUEST_2 = 2;
    private TextToSpeech mSpeech;
    private static final String LOG_TAG = ReminderActivity.class.getName();

    private CardScrollView mConfirmCardScrollView;
    private ConfirmAdapter  mConfirmAdapter;

    private CardScrollAdapter mAdapter;
    private int currentAlarm = -1;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
            }
        });
        createCards();
        mAdapter = new ReminderScrollAdapter();
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();

        CardBuilder cbNo =new CardBuilder(this, CardBuilder.Layout.MENU).setIcon(R.drawable.ic_no_50).setText(R.string.no);
        CardBuilder cbYes =new CardBuilder(this, CardBuilder.Layout.MENU).setIcon(R.drawable.ic_done_50).setText(R.string.yes);
        mConfirmAdapter = new ConfirmAdapter(this, cbYes, cbNo);
        mConfirmCardScrollView = new CardScrollView(this);
        mConfirmCardScrollView.setAdapter(mConfirmAdapter);

        mConfirmCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                playClickSound();
                invalidateOptionsMenu();
                if (position == 0)
                {
                    // delete note
                    deleteAlarm();
                    AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audio.playSoundEffect(Sounds.SUCCESS);
                }
                showReminderView();

            }
        });





        showReminderView();
    }

    private void deleteAlarm() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor e = mPrefs.edit();
        String key = (currentAlarm ==1) ? "firstAlarm" : "secondAlarm";
        e.putString(key, "None");
        e.apply();
        AlarmHelper.cancelAlarm(this,currentAlarm);
        refresh();
    }

    private void playClickSound() {
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.playSoundEffect(Sounds.TAP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reminder, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_remove_reminder:
                showDeleteReminderConfirmation();
                return true;
            case R.id.menu_edit_reminder:
                int requestCode = (currentAlarm ==1) ?  SPEECH_REQUEST_1 :SPEECH_REQUEST_2;
                startSpeechForTime(requestCode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Different type of activities can be shown, when tapped on a card.
     */
    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.TAP);
                selectedAlarm(position+1);
            }
        });
    }

    private boolean isAlarmSet(int alarmNumber) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String key = (alarmNumber ==1) ? "firstAlarm" : "secondAlarm";
        String alarm = mPrefs.getString(key, "None");
        return AlarmHelper.isTime(alarm);
    }

    private void showReminderView() {
        mCardScroller.activate();
        mCardScroller.setHorizontalScrollBarEnabled(true);
        mConfirmCardScrollView.deactivate();
        setContentView(mCardScroller);
    }

    private void showDeleteReminderConfirmation() {
        mSpeech.speak("Are you sure you want to delete this reminder?", TextToSpeech.QUEUE_FLUSH, null);
        mCardScroller.deactivate();
        mConfirmCardScrollView.activate();
        setContentView(mConfirmCardScrollView);
    }
    private void selectedAlarm(int alarmNumber) {

        if (isAlarmSet(alarmNumber)) {
            // edit alarm
            currentAlarm = alarmNumber;
            invalidateOptionsMenu();
            openOptionsMenu();
        }
        else
        {
            // add alarm
            int requestCode = (alarmNumber ==1) ?  SPEECH_REQUEST_1 :SPEECH_REQUEST_2;
            startSpeechForTime(requestCode);
        }


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

    @Override
    protected void onDestroy()
    {
        mSpeech.shutdown();
        mSpeech = null;
        super.onDestroy();
    }


    private String getAlarmFootnote(String alarm, String defaultText) {
        if (AlarmHelper.isTime(alarm)) {
            return defaultText;
        }
        else
        {
            return "Tap to Add Reminder";
        }
    }

    private void startSpeechForTime(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say time (Ex: 9:30 am)");
        startActivityForResult(intent, requestCode);
    }

    private void processTime(String time, int alarmNumber) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor e = mPrefs.edit();
        String key = (alarmNumber ==1) ? "firstAlarm" : "secondAlarm";
        e.putString(key, time);
        e.apply();
        AlarmHelper.addAlarm(this, alarmNumber, time);
//        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        am.playSoundEffect(Sounds.SUCCESS);
        refresh();
    }

    private void refresh() {
        createCards();
        mAdapter.notifyDataSetChanged();
    }

    private void reportBadTime() {
        mSpeech.speak("I didn't recognize that time. Try again.", TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int alarmNumber = 1;
            if (requestCode == SPEECH_REQUEST_2) {
                alarmNumber = 2;
            }

            List<String> results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if (AlarmHelper.isTime(spokenText)) {
                processTime(spokenText, alarmNumber);
            }
            else {
                reportBadTime();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createCards() {
        mCards = new ArrayList<CardBuilder>();

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String firstAlarm = mPrefs.getString("firstAlarm", "None");
        String secondAlarm = mPrefs.getString("secondAlarm", "None");

        CardBuilder cbReminder1 = new CardBuilder(this, CardBuilder.Layout.MENU);
        cbReminder1.setIcon(R.drawable.ic_action_alarms);
        cbReminder1.setText(firstAlarm);
        cbReminder1.setFootnote(getAlarmFootnote(firstAlarm, "1st Reminder"));
        mCards.add(cbReminder1);

        CardBuilder cbReminder2 = new CardBuilder(this, CardBuilder.Layout.MENU);
        cbReminder2.setIcon(R.drawable.ic_action_alarms);
        cbReminder2.setText(secondAlarm);
        cbReminder2.setFootnote(getAlarmFootnote(secondAlarm, "2nd Reminder"));
        mCards.add(cbReminder2);

    }

    private class ReminderScrollAdapter extends CardScrollAdapter {

        @Override
        public int getPosition(Object item) {
            return mCards.indexOf(item);
        }

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return CardBuilder.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position){
            return mCards.get(position).getItemViewType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).getView(convertView, parent);
        }
    }
}
