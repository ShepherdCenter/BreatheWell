package org.shepherd.breathewell;

import android.content.SharedPreferences;

import org.shepherd.breathewell.settings.option.PreferenceOption;
import org.shepherd.breathewell.settings.types.ChooserPreference;

import java.util.List;

/**
 * Created by scott on 7/6/2015.
 */
public class MusicPreference extends ChooserPreference {

    public interface OnMusicSelectedListener {
        void onMusicSelected();
    }

    private OnMusicSelectedListener onMusicSelectedListener;

    public MusicPreference(SharedPreferences prefs, String key, String title, String speech, List<PreferenceOption> options, OnMusicSelectedListener onMusicSelectedListener) {
        super(prefs, key, title, speech, options);
        this.onMusicSelectedListener = onMusicSelectedListener;
    }

    @Override
    public void onOptionSelected(int index){
        super.onOptionSelected(index);
        if (index > 6) {
            this.onMusicSelectedListener.onMusicSelected();
        }

    }
}
