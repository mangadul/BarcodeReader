package mangadul.project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class UserSetting extends PreferenceActivity implements 
        SharedPreferences.OnSharedPreferenceChangeListener {

        public static final String PREFS_NAME = "PdamPrefsFile";

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.layout.setting);
        }

        public void onSharedPreferenceChanged(SharedPreferences sp, String key)
        {
            updatePref(findPreference(key));
        }

        private void updatePref(final Preference p) {
            
                if (p instanceof ListPreference) {
                    final ListPreference listPref = (ListPreference) p;
                    listPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        public boolean onPreferenceClick(Preference arg0) {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(p.getKey(), listPref.getValue());
                            editor.commit();
                            return true;
                        }
                    });
                }

                if (p instanceof EditTextPreference) {
                    final EditTextPreference editTextPref = (EditTextPreference) p;
                    //p.setSummary(editTextPref.getText());
                    p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        public boolean onPreferenceClick(Preference arg0) {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(p.getKey(), editTextPref.getText().toString());
                            editor.commit();
                            return true;
                        }
                    });
                }

                if (p instanceof CheckBoxPreference) {
                    final CheckBoxPreference checkBoxPref = (CheckBoxPreference) p;
                    p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        public boolean onPreferenceClick(Preference arg0) {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(p.getKey(), checkBoxPref.isChecked());
                            editor.commit();
                            return true;
                        }
                    });
                }
                
        }

        @Override
        protected void onResume(){
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        protected void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop()
        {
            super.onStop();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();
        }

}