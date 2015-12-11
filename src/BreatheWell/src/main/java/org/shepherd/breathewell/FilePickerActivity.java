package org.shepherd.breathewell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import org.shepherd.breathewell.adapter.CardAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by scott on 7/6/2015.
 */
public class FilePickerActivity extends Activity {



    /**
     * {@link com.google.android.glass.widget.CardScrollView} to use as the main content view.
     */
    private CardScrollView mCardScroller;

    private static final String LOG_TAG = FilePickerActivity.class.getName();

    private CardScrollAdapter mAdapter;
    private File currentFolder = null;
    private List<String> pictures = new ArrayList<String>();
    private String fileType = "";
    private String path = "";


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path =  extras.getString("path");
            fileType =  extras.getString("filetype");
        }

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
                Intent data = new Intent();
                data.putExtra("file", pictures.get(position));
                setResult(RESULT_OK, data);
                // Play sound.
                if (bPlaySound) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(soundEffect);
                }
                finish();
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


        String startingFolder = Environment.getExternalStorageDirectory().getPath()+"/" + path;
        currentFolder = new File(startingFolder);
        if (currentFolder.exists()) {

            File[] files = currentFolder.listFiles();
            if (files != null) {
                Arrays.sort(files, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        return ((File) o1).getName().compareTo(((File) o2).getName());
                    }

                });

                for(File file : files){
                    if (!file.isDirectory()){

                        pictures.add(file.getPath());
                        CardBuilder cbFile = new CardBuilder(this, CardBuilder.Layout.MENU);
                        cbFile.setText(file.getName());
                        cbFile.setFootnote(file.getPath());
                        cards.add(cbFile);

                    }
                }

            }
        }
        else {
            currentFolder.mkdir();
        }

        if (cards.size() == 0)  {
            CardBuilder cbFile = new CardBuilder(this, CardBuilder.Layout.MENU);
            cbFile.setFootnote("Copy " + fileType + " to '" + path + "'");
            cbFile.setText("No files found");
            cards.add(cbFile);
        }

        return cards;
    }

}
