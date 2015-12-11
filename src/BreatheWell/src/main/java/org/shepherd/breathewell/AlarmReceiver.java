package org.shepherd.breathewell;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.widget.CardBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by scott on 3/14/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static LiveCard liveCard;
    private static final String LOG_TAG = AlarmReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(LOG_TAG, String.format("on Receive"));

        Bundle extras = intent.getExtras();
        if (extras != null) {
            if(extras.containsKey("publish")){
                Log.i(LOG_TAG, String.format("Publish card"));
                publishCard(context);
            }
            if(extras.containsKey("unpublish")){
                Log.i(LOG_TAG, String.format("Unpublish card"));
                unpublishCard();
            }
        }
    }

    private void publishCard(Context context) {
        String cardId = "breathewell";
        liveCard = new LiveCard(context.getApplicationContext(), cardId);            	            //
        updateLiveCard(context);
        liveCard.publish(LiveCard.PublishMode.REVEAL);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.playSoundEffect(Sounds.SUCCESS);
    }

    private void unpublishCard() {
        if (liveCard != null) {
            if (liveCard.isPublished()) {
                liveCard.unpublish();
                liveCard = null;
            }
        } else
        {
            Log.i(LOG_TAG, String.format("livecard is null"));
        }
    }

    private void updateLiveCard(Context context) {

        CardBuilder cb;

        RemoteViews vLiveCard;

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean bRate = mPrefs.getBoolean("rate", true);

        vLiveCard = this.getInitialView(context);
        Intent intent;
        if (bRate) {
            intent = new Intent(context, RateActivity.class);
        } else {
            intent = new Intent(context, BreatheActivity.class);
        }
        liveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
        liveCard.setViews(vLiveCard);
        //    liveCard.navigate();

    }
    private RemoteViews getInitialView(Context context)
    {
        String cardText = "";
        String cardFooter = null;
        CardBuilder cb = new CardBuilder(context, CardBuilder.Layout.TEXT);
        cb.setText("Time to Breathe!");
        cb.setFootnote("Reminder from the BreatheWell app.");

        return cb.getRemoteViews();
    }
}