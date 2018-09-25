package com.darryncampbell.rndatawedgeintents;

import android.content.Intent;
import android.content.ComponentName;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.net.Uri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Iterator;
import java.lang.SecurityException;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RNDataWedgeIntentsModule extends ReactContextBaseJavaModule implements Observer, LifecycleEventListener {

    private static final String TAG = RNDataWedgeIntentsModule.class.getSimpleName();

    //  THESE ACTIONS ARE DEPRECATED, PLEASE SPECIFY THE ACTION AS PART OF THE CALL TO sendBroadcastWithExtras
    private static final String ACTION_SOFTSCANTRIGGER = "com.symbol.datawedge.api.ACTION_SOFTSCANTRIGGER";
    private static final String ACTION_SCANNERINPUTPLUGIN = "com.symbol.datawedge.api.ACTION_SCANNERINPUTPLUGIN";
    private static final String ACTION_ENUMERATESCANNERS = "com.symbol.datawedge.api.ACTION_ENUMERATESCANNERS";
    private static final String ACTION_SETDEFAULTPROFILE = "com.symbol.datawedge.api.ACTION_SETDEFAULTPROFILE";
    private static final String ACTION_RESETDEFAULTPROFILE = "com.symbol.datawedge.api.ACTION_RESETDEFAULTPROFILE";
    private static final String ACTION_SWITCHTOPROFILE = "com.symbol.datawedge.api.ACTION_SWITCHTOPROFILE";
    private static final String EXTRA_PARAMETER = "com.symbol.datawedge.api.EXTRA_PARAMETER";
    private static final String EXTRA_PROFILENAME = "com.symbol.datawedge.api.EXTRA_PROFILENAME";
    //  Intent extra parameters
    private static final String START_SCANNING = "START_SCANNING";
    private static final String STOP_SCANNING = "STOP_SCANNING";
    private static final String TOGGLE_SCANNING = "TOGGLE_SCANNING";
    private static final String ENABLE_PLUGIN = "ENABLE_PLUGIN";
    private static final String DISABLE_PLUGIN = "DISABLE_PLUGIN";
    //  Enumerated Scanner receiver
    private static final String ACTION_ENUMERATEDLISET = "com.symbol.datawedge.api.ACTION_ENUMERATEDSCANNERLIST";
    private static final String KEY_ENUMERATEDSCANNERLIST = "DWAPI_KEY_ENUMERATEDSCANNERLIST";
    //  END DEPRECATED PROPERTIES

    //  Scan data receiver
    private static final String RECEIVED_SCAN_SOURCE = "com.symbol.datawedge.source";
    private static final String RECEIVED_SCAN_DATA = "com.symbol.datawedge.data_string";
    private static final String RECEIVED_SCAN_TYPE = "com.symbol.datawedge.label_type";
	//  The previously registered receiver (if any)
	private String registeredAction = null;
	private String registeredCategory = null;

    private ReactApplicationContext reactContext;

    public RNDataWedgeIntentsModule(ReactApplicationContext reactContext) {
      super(reactContext);
      this.reactContext = reactContext;
      reactContext.addLifecycleEventListener(this);
      Log.v(TAG, "Constructing React native DataWedge intents module");

      //  Register a broadcast receiver for the Enumerate Scanners intent
      ObservableObject.getInstance().addObserver(this);
    }

    @Override
    public void onHostResume() {
        //Log.v(TAG, "Host Resume");
      IntentFilter filter = new IntentFilter();
      filter.addAction(ACTION_ENUMERATEDLISET);
      reactContext.registerReceiver(myEnumerateScannersBroadcastReceiver, filter);
	  if (this.registeredAction != null)
		  registerReceiver(this.registeredAction, this.registeredCategory);
    }

    @Override
    public void onHostPause() {
        //Log.v(TAG, "Host Pause");
      try
      {
          this.reactContext.unregisterReceiver(myEnumerateScannersBroadcastReceiver);
      }
      catch (IllegalArgumentException e)
      {
          //  Expected behaviour if there was not a previously registered receiver.
      }
      try
      {
          this.reactContext.unregisterReceiver(scannedDataBroadcastReceiver);
      }
      catch (IllegalArgumentException e)
      {
          //  Expected behaviour if there was not a previously registered receiver.
      }
    }

    @Override
    public void onHostDestroy() {
        // Activity `onDestroy`
        Log.v(TAG, "Host Destroy");
    }

    @Override
    public String getName() {
      return "DataWedgeIntents";
    }


    @Override
    public Map<String, Object> getConstants() {
      final Map<String, Object> constants = new HashMap<>();
      //  These are the constants available to the caller
      constants.put("ACTION_SOFTSCANTRIGGER", ACTION_SOFTSCANTRIGGER);
      constants.put("ACTION_SCANNERINPUTPLUGIN", ACTION_SCANNERINPUTPLUGIN);
      constants.put("ACTION_ENUMERATESCANNERS", ACTION_ENUMERATESCANNERS);
      constants.put("ACTION_SETDEFAULTPROFILE", ACTION_SETDEFAULTPROFILE);
      constants.put("ACTION_RESETDEFAULTPROFILE", ACTION_RESETDEFAULTPROFILE);
      constants.put("ACTION_SWITCHTOPROFILE", ACTION_SWITCHTOPROFILE);
      constants.put("START_SCANNING", START_SCANNING);
      constants.put("STOP_SCANNING", STOP_SCANNING);
      constants.put("TOGGLE_SCANNING", TOGGLE_SCANNING);
      constants.put("ENABLE_PLUGIN", ENABLE_PLUGIN);
      constants.put("DISABLE_PLUGIN", DISABLE_PLUGIN);
      return constants;
    }

    @ReactMethod
    public void sendIntent(String action, String parameterValue)
    {
        Log.v(TAG, "Sending Intent with action: " + action + ", parameter: [" + parameterValue + "]");
        //  Some DW API calls use a different paramter name, abstract this from the caller.
        String parameterKey = EXTRA_PARAMETER;
        if (action.equalsIgnoreCase(ACTION_SETDEFAULTPROFILE) || 
            action.equalsIgnoreCase(ACTION_RESETDEFAULTPROFILE) || 
            action.equalsIgnoreCase(ACTION_SWITCHTOPROFILE))
                parameterKey = EXTRA_PROFILENAME;

        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        if (parameterValue != null && parameterValue.length() > 0)
            dwIntent.putExtra(parameterKey, parameterValue);
        this.reactContext.sendBroadcast(dwIntent);
    }

    @ReactMethod
    public void sendBroadcastWithExtras(ReadableMap obj)
    {
        String action = obj.hasKey("action") ? obj.getString("action") : null;
        Intent i = new Intent();
        if (action != null)
            i.setAction(action);

        Map<String, Object> intentMap = recursivelyDeconstructReadableMap(obj);
        Map<String, Object> extrasMap = null;
        if (intentMap.containsKey("extras") && intentMap.get("extras") != null &&
                intentMap.get("extras") instanceof Map)
            extrasMap = (Map<String, Object>) intentMap.get("extras");

        for (String key : extrasMap.keySet()) {
            Object value = extrasMap.get(key);
            String valueStr = String.valueOf(value);
            // If type is text html, the extra text must sent as HTML
            if (value instanceof Boolean) {
                i.putExtra(key, Boolean.valueOf(valueStr));
            } else if(value instanceof Integer) {
                i.putExtra(key, Integer.valueOf(valueStr));
            } else if(value instanceof Long) {
                i.putExtra(key, Long.valueOf(valueStr));
            } else if(value instanceof Double) {
                i.putExtra(key, Double.valueOf(valueStr));
            } else {
                i.putExtra(key, valueStr);
            }
        }

        this.reactContext.sendBroadcast(i);    
    }

    //  Credit: https://github.com/facebook/react-native/issues/4655
    private Map<String, Object> recursivelyDeconstructReadableMap(ReadableMap readableMap) {
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        Map<String, Object> deconstructedMap = new HashMap<>();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType type = readableMap.getType(key);
            switch (type) {
                case Null:
                    deconstructedMap.put(key, null);
                    break;
                case Boolean:
                    deconstructedMap.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    deconstructedMap.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    deconstructedMap.put(key, readableMap.getString(key));
                    break;
                case Map:
                    deconstructedMap.put(key, recursivelyDeconstructReadableMap(readableMap.getMap(key)));
                    break;
                case Array:
                    deconstructedMap.put(key, recursivelyDeconstructReadableArray(readableMap.getArray(key)));
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
            }

        }
        return deconstructedMap;
    }

    //  Credit: https://github.com/facebook/react-native/issues/4655
    private List<Object> recursivelyDeconstructReadableArray(ReadableArray readableArray) {
        List<Object> deconstructedList = new ArrayList<>(readableArray.size());
        for (int i = 0; i < readableArray.size(); i++) {
            ReadableType indexType = readableArray.getType(i);
            switch(indexType) {
                case Null:
                    deconstructedList.add(i, null);
                    break;
                case Boolean:
                    deconstructedList.add(i, readableArray.getBoolean(i));
                    break;
                case Number:
                    deconstructedList.add(i, readableArray.getDouble(i));
                    break;
                case String:
                    deconstructedList.add(i, readableArray.getString(i));
                    break;
                case Map:
                    deconstructedList.add(i, recursivelyDeconstructReadableMap(readableArray.getMap(i)));
                    break;
                case Array:
                    deconstructedList.add(i, recursivelyDeconstructReadableArray(readableArray.getArray(i)));
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object at index " + i + ".");
            }
        }
        return deconstructedList;
    }

    @ReactMethod
    public void registerReceiver(String action, String category)
    {
        Log.d(TAG, "Registering an Intent filter for action: " + action);
		this.registeredAction = action;
		this.registeredCategory = category;
        //  User has specified the intent action and category that DataWedge will be reporting
        try
        {
            this.reactContext.unregisterReceiver(scannedDataBroadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            //  Expected behaviour if there was not a previously registered receiver.
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        if (category != null && category.length() > 0)
          filter.addCategory(category);
        this.reactContext.registerReceiver(scannedDataBroadcastReceiver, filter);
    }

    //  Broadcast receiver for the response to the Enumerate Scanner API
    public BroadcastReceiver myEnumerateScannersBroadcastReceiver = new BroadcastReceiver() 
    {    
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Received Broadcast from DataWedge API - Enumerate Scanners");
            ObservableObject.getInstance().updateValue(intent);
        }
    };

    //  Broadcast receiver for the DataWedge intent being sent from Datawedge.  
    //  Note: DW must be configured to send broadcast intents
    public BroadcastReceiver scannedDataBroadcastReceiver = new BroadcastReceiver() 
    {    
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Received Broadcast from DataWedge API - Scanner");
            ObservableObject.getInstance().updateValue(intent);
        }
    };

    //  Sending events to JavaScript as defined in the native-modules documentation.  
    //  Note: Callbacks can only be invoked a single time so are not a suitable interface for barcode scans.
    private void sendEvent(ReactContext reactContext,
                       String eventName,
                       WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    //  http://stackoverflow.com/questions/28083430/communication-between-broadcastreceiver-and-activity-android#30964385
    @Override
    public void update(Observable observable, Object data) 
    {            
      Intent intent = (Intent)data;
      String action = intent.getAction();
      if (action.equals(ACTION_ENUMERATEDLISET)) 
      {
          Bundle b = intent.getExtras();
          String[] scanner_list = b.getStringArray(KEY_ENUMERATEDSCANNERLIST);
          WritableArray userFriendlyScanners = new WritableNativeArray();
          for (int i = 0; i < scanner_list.length; i++)
          {
              userFriendlyScanners.pushString(scanner_list[i]);
          }
          try
          {
            WritableMap enumeratedScannersObj = new WritableNativeMap();
            enumeratedScannersObj.putArray("Scanners", userFriendlyScanners);
            sendEvent(this.reactContext, "enumerated_scanners", enumeratedScannersObj);
          }
          catch (Exception e)
          {
              Toast.makeText(this.reactContext, "Error returning scanners", Toast.LENGTH_LONG).show();
              e.printStackTrace();
          }
      }
      else
      {
          //  Intent from the scanner (barcode has been scanned)
          String decodedSource = intent.getStringExtra(RECEIVED_SCAN_SOURCE);
          String decodedData = intent.getStringExtra(RECEIVED_SCAN_DATA);
          String decodedLabelType = intent.getStringExtra(RECEIVED_SCAN_TYPE);

          WritableMap scanData = new WritableNativeMap();
          scanData.putString("source", decodedSource);
          scanData.putString("data", decodedData);
          scanData.putString("labelType", decodedLabelType);
          sendEvent(this.reactContext, "barcode_scan", scanData);
      }
    }
}
