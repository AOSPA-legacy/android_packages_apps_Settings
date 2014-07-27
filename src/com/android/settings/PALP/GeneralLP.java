/*
 * Copyright (C) 2014 The ParanoidAndroid Legacy Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.PALP;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import com.android.settings.hardware.VibratorIntensity;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.R;

public class GeneralLP extends SettingsPreferenceFragment {

    private static final String KEY_SENSORS_MOTORS_CATEGORY = "sensors_motors_category";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.general_lp_settings);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (!VibratorIntensity.isSupported() || vibrator == null || !vibrator.hasVibrator()) {
            removePreference(KEY_SENSORS_MOTORS_CATEGORY);
        }

    }
}
