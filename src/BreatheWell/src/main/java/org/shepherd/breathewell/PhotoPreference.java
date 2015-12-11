package org.shepherd.breathewell;

import android.content.SharedPreferences;

import org.shepherd.breathewell.settings.option.PreferenceOption;
import org.shepherd.breathewell.settings.types.ChooserPreference;

import java.util.List;

/**
 * Created by scott on 5/17/2015.
 */
public class PhotoPreference extends ChooserPreference {

    public interface OnPhotoSelectedListener {
        void onPhotoSelected();
    }

    private OnPhotoSelectedListener onPhotoSelectedListener;

    public PhotoPreference(SharedPreferences prefs, String key, String title, String speech, List<PreferenceOption> options, OnPhotoSelectedListener onPhotoSelectedListener) {
        super(prefs, key, title, speech, options);
        this.onPhotoSelectedListener = onPhotoSelectedListener;
    }

    @Override
    public void onOptionSelected(int index){
        super.onOptionSelected(index);
        if (index > 7) {
            this.onPhotoSelectedListener.onPhotoSelected();
        }
        //putInt(index);
        // launch camera ?
    }

}
