/**
 *
 * @author Achraf Gazdar
 */

package com.phonetostbapp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;

import android.graphics.Color;

import android.widget.TextView;

public class aboutDialog extends Dialog{
private static Context mContext = null;
public aboutDialog(Context context) {
super(context);
mContext = context;


}

/* Standard Android on create method that gets called when the activity initialized.
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.aboutdialog);

        TextView tv = (TextView)findViewById(R.id.legal_text);
        tv.setText(readRawTextFile(R.raw.legal));
        tv = (TextView)findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
        tv.setLinkTextColor(Color.WHITE);
        Linkify.addLinks(tv, Linkify.ALL);
    }

    public static String readRawTextFile(int id) {
        InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();
        try {

            while (( line = buf.readLine()) != null) text.append(line);
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
