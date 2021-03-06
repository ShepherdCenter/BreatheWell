package org.shepherd.breathewell.settings.types;
import java.util.List;

import org.shepherd.breathewell.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import org.shepherd.breathewell.settings.option.PreferenceOption;

public class TogglePreference extends AbstractPreference {

	boolean mEnabled = false;

	public TogglePreference(SharedPreferences prefs, String key, String title) {
		super(prefs, key, title);
		mEnabled = getBoolean();
	}

	public TogglePreference(SharedPreferences prefs, String key, String title, boolean defaultValue) {
		super(prefs, key, title);
		mEnabled = getBoolean(defaultValue);
	}

    @Override
    public String getValue() {
        boolean b = getBoolean();
        return (b) ? "true" : "false";
    }

	@Override
	public boolean onSelect() {
		mEnabled = !mEnabled;
		putBoolean(mEnabled);

		// Return true because everything is done
		return true;
	}

	@Override
	public View getCard(Context context) {
		// Update the image to either be a check mark
		// or an X
		setToggleImage();
		
		return getDefaultCard(context);
	}

	@Override
	public List<PreferenceOption> getOptions() {
		// onSelect returns true, so this will not be called
		return null;
	}
	
	@Override
	public void onOptionSelected(int index){
		// onSelect returns true, so this will not be called
	}
	
	private void setToggleImage(){
		setImageResource((mEnabled) ? (R.drawable.ic_done_50) : (R.drawable.ic_no_50));
	}

}
