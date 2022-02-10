package com.sprd.engineermode;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import com.sprd.engineermode.R;

public class EMClickEditTextPreference extends EditTextPreference implements OnClickListener {

    final static String TAG = "EMClickEditTextPreference";

    private Context mContext;
    private int mId;
    private Button mButton;
    private RelativeLayout mRelativeLayout;
    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public EMClickEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public EMClickEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * @param context
     */
    public EMClickEditTextPreference(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view = super.getView(convertView, parent);

        View mView = view.findViewById(R.id.relativeLayout);
        Log.d(TAG, "getView");
        if ((mView != null) && mView instanceof RelativeLayout) {
            Log.d(TAG, "getView RelativeLayout");
            mRelativeLayout = (RelativeLayout) mView;
            //mRelativeLayout.setOnLongClickListener(this);
        }

        View widget = view.findViewById(R.id.x_button);
        Log.d(TAG, "getView");
        if ((widget != null) && widget instanceof Button) {
            Log.d(TAG, "getView Button");
            mButton = (Button) widget;
            mButton.setOnClickListener(this);
        }

        View textLayout = view.findViewById(R.id.text_layout);
        if ((textLayout != null) && textLayout instanceof TextView) {
            ((TextView) textLayout).setText(getTitle());
        }

        View textSummary = view.findViewById(R.id.text_summary);
        if ((textSummary != null) && textSummary instanceof TextView && getSummary() != null) {
            ((TextView) textSummary).setText(getSummary());
            ((TextView) textSummary).setVisibility(View.VISIBLE);
        }
        view.setTag(mId);
        return view;
    }

    private void init() {
        setLayoutResource(R.layout.click_editpreference_layout);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onClick");
        switch (v.getId()) {
            case R.id.x_button:
                Log.d(TAG, "onClick x_button");
                setText(getDialogMessage().toString());
                //createDialog(getSummary().toString());
                break;
            default:
                break;
        }
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    private void createDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.function_introduction))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {

                            }
                        }).create();
        alertDialog.show();
    }
}
