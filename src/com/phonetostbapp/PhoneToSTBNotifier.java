/**
 *
 * @author Achraf Gazdar
 */
package com.phonetostbapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhoneToSTBNotifier extends Activity
{
    private final int ID_MENU_SETTINGS = 1;
    private final int ID_MENU_EXIT = 2;
    private final int ID_MENU_ABOUT = 3;
    private final int ID_MENU_RUN = 4;
    public static final String EXIT_INTENT ="com.phonetostbapp.intent.action.EXIT";
    private Dialog settingsDialog;
    private SharedPreferences mySettings;
    private FileOutputStream outSettings;
    private FileInputStream inSettings;
    private String name="";
    private String ipAdress="";
    private String port="";
    private String login="";
    private String pw="";
    private Boolean displayCallerName=false;
    private Boolean isEnigmaOne=false;
    private Menu mainMenu;
    //private EditText ipText;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Reading our config file if it existes or create a dumm one
        try {
            //restore the settings if any
            SharedPreferences myPrefs = getSharedPreferences("myPrefs", 4);
            this.ipAdress+=myPrefs.getString("IP", "");
            this.name+=myPrefs.getString("NAME", "");
            this.port+=myPrefs.getString("PORT", "");
            this.login+=myPrefs.getString("LOGIN", "");
            this.pw+=myPrefs.getString("PW", "");
            if(myPrefs.getString("IDDISPLAY", "").equals("yes"))
                  this.displayCallerName=true;
            else
                  this.displayCallerName=false;
                
            if(myPrefs.getString("OSTYPE", "").equals("e1"))
                  this.isEnigmaOne=true;
            else
                  this.isEnigmaOne=false;
            
            
        } catch (Exception ex) {
            
                Logger.getLogger(PhoneToSTBNotifier.class.getName()).log(Level.SEVERE, null, ex);
           
        }
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    //the menu option text is defined in resources
        menu.add(Menu.NONE,ID_MENU_SETTINGS,Menu.NONE,R.string.settingsOption);
        menu.add(Menu.NONE,ID_MENU_RUN,Menu.NONE,R.string.runOption);
        menu.getItem(1).setEnabled(false);
        menu.add(Menu.NONE,ID_MENU_EXIT,Menu.NONE,R.string.exitOption);
        
        //get the MenuItem reference
        MenuItem item = menu.add(Menu.NONE,ID_MENU_ABOUT,Menu.NONE,R.string.aboutOption);
        //set the shortcut
        item.setShortcut('5', 'x');
        mainMenu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	//check selected menu item
    	if(item.getItemId() == ID_MENU_EXIT)
    	{
    		//close the Activity
            new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Phone to STB Notifier")
                .setMessage("Are you sure you want to close this application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i.setAction(EXIT_INTENT);
                            sendBroadcast(i);
                            finish();    
                        }

                    })
                .setNegativeButton("No", null)
                .show();
                
    		return true;
    	} 
        if(item.getItemId() == ID_MENU_SETTINGS)
        {
            this.showSettingsDialog();
            return true;            
        }
        if(item.getItemId() == ID_MENU_ABOUT)
        {
            aboutDialog about = new aboutDialog(this); 
            about.setTitle("About PhoneToSTBNotifier");
            about.show();
            return true;            
        }
        if(item.getItemId() == ID_MENU_RUN){
            //Starting our notifier service
            startService(new Intent(toSTBnotifierService.class.getName()));
            finish();    
            return true;
        }
    	return false;
    }
    private void showSettingsDialog(){
        settingsDialog = new Dialog(PhoneToSTBNotifier.this);
        settingsDialog.setContentView(R.layout.settingsdialog);
        settingsDialog.setTitle("Settings");
        settingsDialog.setCancelable(true);
        
        EditText nameText = (EditText)settingsDialog.findViewById(R.id.name);
        nameText.setText(this.name);
        
        EditText ipText = (EditText)settingsDialog.findViewById(R.id.ipadress);
        ipText.setText(this.ipAdress);
        
        
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                    android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) { 
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };
        ipText.setFilters(filters);
        
        
        EditText portText = (EditText)settingsDialog.findViewById(R.id.tcpport);
        portText.setText(this.port);
        
        EditText loginText = (EditText) settingsDialog.findViewById(R.id.textlogin);
        loginText.setText(this.login);
        
        EditText pwText = (EditText) settingsDialog.findViewById(R.id.pw);
        pwText.setText(this.pw);
        
        RadioButton yesRadio = (RadioButton)settingsDialog.findViewById(R.id.radio_yes);
        if(this.displayCallerName) yesRadio.setChecked(true);
        yesRadio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCallerName = true;
            }
        });
        
        RadioButton noRadio = (RadioButton)settingsDialog.findViewById(R.id.radio_no);
        if(!this.displayCallerName) noRadio.setChecked(true);
        noRadio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCallerName = false;        
            }
        });
        
        RadioButton enigma1 = (RadioButton)settingsDialog.findViewById(R.id.radio_e1);
        if(this.isEnigmaOne) enigma1.setChecked(true);
        enigma1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnigmaOne = true;        
            }
        });
        
        RadioButton enigma2 = (RadioButton)settingsDialog.findViewById(R.id.radio_e2);
        if(!this.isEnigmaOne) enigma2.setChecked(true);
        enigma2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnigmaOne = false;        
            }
        });
        
        Button buttonSave = (Button) settingsDialog.findViewById(R.id.Button00);
        buttonSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mySettings = getSharedPreferences("myPrefs",4);
                    SharedPreferences.Editor prefsEditor = mySettings.edit();
                    
                    prefsEditor.clear();
                    
                    EditText nameText = (EditText)settingsDialog.findViewById(R.id.name);
                    prefsEditor.putString("NAME", nameText.getText().toString());
                    name = nameText.getText().toString();
                    
                    EditText ipText = (EditText)settingsDialog.findViewById(R.id.ipadress);
                    prefsEditor.putString("IP", ipText.getText().toString());
                    ipAdress = ipText.getText().toString();
                   
                    EditText portText = (EditText)settingsDialog.findViewById(R.id.tcpport);
                    prefsEditor.putString("PORT", portText.getText().toString());
                    port = portText.getText().toString();
                   
                    EditText loginText = (EditText) settingsDialog.findViewById(R.id.textlogin);
                    prefsEditor.putString("LOGIN", loginText.getText().toString());
                    login = loginText.getText().toString();
                    
                    EditText pwText = (EditText) settingsDialog.findViewById(R.id.pw);
                    prefsEditor.putString("PW", pwText.getText().toString());
                    pw = pwText.getText().toString();
                    
                    if (displayCallerName){
                        prefsEditor.putString("IDDISPLAY", "yes");
                    }else{
                        prefsEditor.putString("IDDISPLAY", "no");
                    }
                    
                    if(isEnigmaOne){
                        prefsEditor.putString("OSTYPE", "e1");
                    }else{
                        prefsEditor.putString("OSTYPE", "e2");
                    }
                                        
                    prefsEditor.commit();
                    
                    mainMenu.getItem(1).setEnabled(true);
                    settingsDialog.dismiss();  
    
                } catch (Exception ex) {
                    Logger.getLogger(PhoneToSTBNotifier.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
            }
        });
        
        Button buttonCancel = (Button) settingsDialog.findViewById(R.id.Button01);
        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();        
            }
        });
                //now that the dialog is set up, it's time to show it    
        settingsDialog.show();
    }
    
    
}
