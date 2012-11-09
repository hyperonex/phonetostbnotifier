/**
 *
 * @author Achraf Gazdar
 */

package com.phonetostbapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class toSTBnotifierService extends Service{
    private static final String TAG = toSTBnotifierService.class.getSimpleName();
    private String settingsFileName = "phonetostb.cfg";
    private FileInputStream inSettings;
    private String name="";
    private String ipAdress="";
    private String port="";
    private String login="";
    private String pw="";
    private Boolean displayCallerName=false;
    private Boolean isEnigmaOne=true;
    //**************Phonelistener*******************//
    
    public class MyPhoneListener extends PhoneStateListener
    {
        private toSTBnotifierService notifierService;
       
        public MyPhoneListener(toSTBnotifierService service) {
            super();
            notifierService = service;
            
        }
        
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: {
                    this.notifierService.notifyEntringCall(incomingNumber);
                    break;
                }
                case TelephonyManager.CALL_STATE_IDLE: 
                {
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneListener(this), PhoneStateListener.LISTEN_CALL_STATE);
        
        IncomingSMSReceiver smsReceiver = new IncomingSMSReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
        
        stopApplicationReceiver stopAppReceiver = new stopApplicationReceiver(this);
        IntentFilter stopFilter = new IntentFilter();
        stopFilter.addAction(PhoneToSTBNotifier.EXIT_INTENT);
        registerReceiver(stopAppReceiver, stopFilter);

    }
 
  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "Service destroying");
       
  }
  
  public void notifyEntringCall(String incomingNumber){
        try {
            
            SharedPreferences myPrefs = getSharedPreferences("myPrefs", 4);
            this.ipAdress=myPrefs.getString("IP", "");
            this.name=myPrefs.getString("NAME", "");
            this.port=myPrefs.getString("PORT", "");
            this.login=myPrefs.getString("LOGIN", "");
            this.pw=myPrefs.getString("PW", "");
            if(myPrefs.getString("IDDISPLAY", "").equals("yes"))
                  this.displayCallerName=true;
            else
                  this.displayCallerName=false;
                
            if(myPrefs.getString("OSTYPE", "").equals("e1"))
                  this.isEnigmaOne=true;
            else
                  this.isEnigmaOne=false;
            String STBuri; 
            /* Enigma 1 based OS */
            if(this.isEnigmaOne)
                STBuri = "http://"/*+login.trim()+":"+pw.trim()+"@"*/+ipAdress.trim()+":"+port.trim()+"/control/message?msg=";
            /* Enigma 2 based OS*/
            else
                STBuri = "http://"/*+login.trim()+":"+pw.trim()+"@"*/+ipAdress.trim()+":"+port.trim()+"/web/message?text=";
            
            
            String messageToDisplay=URLEncoder.encode(name+", your phone is ringing");
            
            String callerName = "";
           
            if (displayCallerName){
                String[] projection = new String[] {
                    ContactsContract.PhoneLookup.DISPLAY_NAME};
                Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
                Context context = this.getBaseContext();
                Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
                if (cursor.moveToFirst()){
                    callerName += cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                if (callerName.equals(""))
                        callerName+=incomingNumber;
              
               messageToDisplay+=URLEncoder.encode(". Call from "+callerName);
               
            }
            
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setHeader("Authorization", "Basic "+ Base64.encodeToString((login.trim()+":"+pw.trim()).getBytes(), Base64.NO_WRAP));
            if(this.isEnigmaOne)
                STBuri+=messageToDisplay;
            else
                STBuri+=messageToDisplay+"&type=2";
            
           
            Log.i(TAG, STBuri);
            request.setURI(new URI(STBuri));
            HttpResponse response = client.execute(request);
           /* HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append((line + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, sb.toString());*/
           }catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
      
  }
  
  public void notifyNewSMS(SmsMessage message){
      String incomingNumber = message.getOriginatingAddress();
      if(incomingNumber != null){
            try {
                
            SharedPreferences myPrefs = getSharedPreferences("myPrefs", 4);
            this.ipAdress=myPrefs.getString("IP", "");
            this.name=myPrefs.getString("NAME", "");
            this.port=myPrefs.getString("PORT", "");
            this.login=myPrefs.getString("LOGIN", "");
            this.pw=myPrefs.getString("PW", "");
            if(myPrefs.getString("IDDISPLAY", "").equals("yes"))
                  this.displayCallerName=true;
            else
                  this.displayCallerName=false;
                
            if(myPrefs.getString("OSTYPE", "").equals("e1"))
                  this.isEnigmaOne=true;
            else
                  this.isEnigmaOne=false;
            String STBuri; 
            /* Enigma 1 based OS */
            if(this.isEnigmaOne)
                STBuri = "http://"/*+login.trim()+":"+pw.trim()+"@"*/+ipAdress.trim()+":"+port.trim()+"/control/message?msg=";
            /* Enigma 2 based OS*/
            else
                STBuri = "http://"/*+login.trim()+":"+pw.trim()+"@"*/+ipAdress.trim()+":"+port.trim()+"/web/message?text=";
            
            String messageToDisplay=URLEncoder.encode(name+", you have a message");
            String callerName = "";
           
            if (displayCallerName){
                String[] projection = new String[] {
                    ContactsContract.PhoneLookup.DISPLAY_NAME};
                Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
                Context context = this.getBaseContext();
                Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
                if (cursor.moveToFirst()){
                    callerName += cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                if (callerName.equals(""))
                        callerName+=incomingNumber;
              
               messageToDisplay+=URLEncoder.encode(" from "+callerName);
            }
            
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setHeader("Authorization", "Basic "+ Base64.encodeToString((login.trim()+":"+pw.trim()).getBytes(), Base64.NO_WRAP));
            if(this.isEnigmaOne)
                STBuri+=messageToDisplay;
            else
                STBuri+=messageToDisplay+"&type=2";
            
            Log.i(TAG, STBuri);
            request.setURI(new URI(STBuri));
            HttpResponse response = client.execute(request);
            Log.i(TAG, response.toString());
           }catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
      }
      
  }
  
}
