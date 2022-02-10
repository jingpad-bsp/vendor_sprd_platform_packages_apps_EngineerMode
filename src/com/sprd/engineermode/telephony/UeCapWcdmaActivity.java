package com.sprd.engineermode.telephony;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.exception.EmException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.INetInfoWcdmaUeCap.UeCap;

import java.util.concurrent.Callable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UeCapWcdmaActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "UeCapWcdmaActivity";
    private static final String KEY_16QAM = "ul_16qam";
    private static final String KEY_DBHSDPA = "dbhsdpa";
    private static final String KEY_SNOW3G = "snow3g";
    private static final String KEY_W_DIVERSITY = "w_diversity";
    private static final String KEY_UL_HSDPA = "hsdpa";

    private ITelephonyApi.INetInfoWcdmaUeCap ueCapApi = CoreApi.getTelephonyApi().wcdmaUeCapApi();
    private int simIdx = UeNwCapActivity.mSimIndex;
    private SwitchPreference mPre16qam;
    private SwitchPreference mPreDbhsdpa;
    private SwitchPreference mPreSnow3g;
    private SwitchPreference mPreWdiversity;
    private SwitchPreference mPreUlhsdpa;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_uecap_wcdma);
        mPre16qam = (SwitchPreference)findPreference(KEY_16QAM);
        mPre16qam.setOnPreferenceChangeListener(this);
        mPreDbhsdpa = (SwitchPreference)findPreference(KEY_DBHSDPA);
        mPreDbhsdpa.setOnPreferenceChangeListener(this);
        mPreSnow3g = (SwitchPreference)findPreference(KEY_SNOW3G);
        mPreSnow3g.setOnPreferenceChangeListener(this);
        mPreWdiversity = (SwitchPreference)findPreference(KEY_W_DIVERSITY);
        mPreWdiversity.setOnPreferenceChangeListener(this);
        mPreUlhsdpa = (SwitchPreference)findPreference(KEY_UL_HSDPA);
        mPreUlhsdpa.setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onstart");
        super.onStart();
        UeCap ueCap;
        try {
            ueCap = ueCapApi.getUeCap(simIdx);
        } catch (EmException e){
            e.printStackTrace();
            ueCap = new UeCap();
        }

        mPre16qam.setChecked(ueCap.ul16Qam);
        mPreDbhsdpa.setChecked(ueCap.dbHsdpa);
        mPreSnow3g.setChecked(ueCap.snow3g);
        mPreWdiversity.setChecked(ueCap.wDiversity);
        mPreUlhsdpa.setChecked(ueCap.hsdpa);
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        Log.d(TAG, "key = " + key);
        if (KEY_16QAM.equals(key)){
            if (mPre16qam.isChecked()) {
                set(mPre16qam, () -> ueCapApi.closeUl16Qam(simIdx));
            } else {
                set(mPre16qam, () -> ueCapApi.openUl16Qam(simIdx));
            }
        } else if (KEY_DBHSDPA.equals(key)) {
            if (mPreDbhsdpa.isChecked()) {
                set(mPreDbhsdpa, () -> ueCapApi.closeDbHsdpa(simIdx));
            } else {
                set(mPreDbhsdpa, () -> ueCapApi.openDbHsdpa(simIdx));
            }
        } else if (KEY_SNOW3G.equals(key)) {
            if (!mPreSnow3g.isChecked()) {
                set(mPreSnow3g, () -> ueCapApi.openSnow3G(simIdx));
            } else {
                set(mPreSnow3g, () -> ueCapApi.closeSnow3G(simIdx));
            }
        } else if (KEY_W_DIVERSITY.equals(key)) {
            if (!mPreWdiversity.isChecked()) {
                set(mPreWdiversity, () -> ueCapApi.openWDiversity(simIdx));
            } else {
                set(mPreWdiversity, () -> ueCapApi.closeWDiversity(simIdx));
            }
        } else if (KEY_UL_HSDPA.equals(key)) {
            if (!mPreUlhsdpa.isChecked()) {
                set(mPreUlhsdpa, () -> ueCapApi.openUlHsdpa(simIdx));
            } else {
                set(mPreUlhsdpa, () -> ueCapApi.closeUlHsdpa(simIdx));
            }
        }
        return false;
    }

    private void set(SwitchPreference pref, Callable<Boolean> callable) {
        disposable = Single.fromCallable(callable)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> {
                if (result) {
                    pref.setChecked(!pref.isChecked());
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Change status is not supported", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
