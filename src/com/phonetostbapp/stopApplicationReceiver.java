/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonetostbapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 * @author root
 */
public class stopApplicationReceiver extends BroadcastReceiver {
    private toSTBnotifierService notifierService; 

    public stopApplicationReceiver(toSTBnotifierService service) {
        notifierService = service;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PhoneToSTBNotifier.EXIT_INTENT)){
            notifierService.stopSelf();
        }
        
    }
    
}
