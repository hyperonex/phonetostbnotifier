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
 * @author Achraf Gazdar
 */
public class BootReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(toSTBnotifierService.class.getName());
        context.startService(serviceIntent); 
  }
}
