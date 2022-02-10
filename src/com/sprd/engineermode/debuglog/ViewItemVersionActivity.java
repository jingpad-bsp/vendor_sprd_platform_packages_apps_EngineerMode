package com.sprd.engineermode.debuglog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.app.ActionBar;
import android.view.MenuItem;
import com.sprd.engineermode.R;
public class ViewItemVersionActivity extends Activity {

    public static final String VERSION = "Show Version";
    public static final String TITLE = "Show Title";
    private TextView mVersionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                , ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        setContentView(R.layout.version_pre);
        mVersionText = (TextView) findViewById(R.id.version_id);
        Intent intent = getIntent();
        if (intent != null) {
            mVersionText.setText(intent.getStringExtra(VERSION));
            setTitle(intent.getStringExtra(TITLE));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}

