package org.shepherd.breathewell;

import android.os.Bundle;
import org.shepherd.breathewell.settings.GlassPreferenceActivity;

public class SettingsActivity  extends GlassPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addTogglePreference("analytics", "Toggle Reporting", true);

        // Builds all the preferences and shows them in a CardScrollView
        buildAndShowOptions();
    }


}
