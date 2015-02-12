package org.apache.cordova.core;

import java.lang.Runnable;
import java.util.Set;
import java.util.Iterator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;
import android.os.Bundle;

public class ParsePlugin extends CordovaPlugin {
    public static final String ACTION_INITIALIZE = "initialize";
    public static final String ACTION_GET_INSTALLATION_ID = "getInstallationId";
    public static final String ACTION_GET_INSTALLATION_OBJECT_ID = "getInstallationObjectId";
    public static final String ACTION_GET_SUBSCRIPTIONS = "getSubscriptions";
    public static final String ACTION_SUBSCRIBE = "subscribe";
    public static final String ACTION_UNSUBSCRIBE = "unsubscribe";
    public static final String ACTION_ON_MESSAGE = "onMessage";

    public static final String TAG = "ParsePlugin";

    private static CallbackContext cbContext = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "action iiiiiiiiiiiiiis: " + action);

        if (action.equals(ACTION_INITIALIZE)) {
            this.initialize(callbackContext, args);
            return true;
        }
        if (action.equals(ACTION_GET_INSTALLATION_ID)) {
            this.getInstallationId(callbackContext);
            return true;
        }
        if (action.equals(ACTION_GET_INSTALLATION_OBJECT_ID)) {
            this.getInstallationObjectId(callbackContext);
            return true;
        }
        if (action.equals(ACTION_GET_SUBSCRIPTIONS)) {
            this.getSubscriptions(callbackContext);
            return true;
        }
        if (action.equals(ACTION_SUBSCRIBE)) {
            this.subscribe(args.getString(0), callbackContext);
            return true;
        }
        if (action.equals(ACTION_UNSUBSCRIBE)) {
            this.unsubscribe(args.getString(0), callbackContext);
            return true;
        }
        if (action.equals(ACTION_ON_MESSAGE)) {
          this.onMessage(callbackContext);
          return true;
        }
        return false;
    }

    private void initialize(final CallbackContext callbackContext, final JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    String appId = args.getString(0);
                    String clientKey = args.getString(1);
                    Parse.initialize(cordova.getActivity(), appId, clientKey);
                    PushService.setDefaultPushCallback(cordova.getActivity(), cordova.getActivity().getClass());
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                    callbackContext.success();
                } catch (JSONException e) {
                    callbackContext.error("JSONException");
                }
            }
        });
    }

    private void getInstallationId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String installationId = ParseInstallation.getCurrentInstallation().getInstallationId();
                callbackContext.success(installationId);
            }
        });
    }

    private void getInstallationObjectId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String objectId = ParseInstallation.getCurrentInstallation().getObjectId();
                callbackContext.success(objectId);
            }
        });
    }

    private void getSubscriptions(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                 Set<String> subscriptions = PushService.getSubscriptions(cordova.getActivity());
                 callbackContext.success(subscriptions.toString());
            }
        });
    }

    private void subscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                PushService.subscribe(cordova.getActivity(), channel, cordova.getActivity().getClass());
                callbackContext.success();
            }
        });
    }

    private void unsubscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                PushService.unsubscribe(cordova.getActivity(), channel);
                callbackContext.success();
            }
        });
    }

    private void getDeviceToken(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
                System.out.println(deviceToken);
                callbackContext.success(deviceToken);
            }
        });
    }

    private void onMessage(final CallbackContext callbackContext) {
        Log.v(TAG, "START LISTENIIIIIIIIIIING");
        cbContext = callbackContext;
    }

    public static void sendExtras(Bundle extras) {
        Log.v(TAG, "SEND Extraaaaaaaas");
        JSONObject message = convertBundleToJson(extras);

        PluginResult result = new PluginResult(PluginResult.Status.OK, message);
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        // Toast.makeText(cordova.getActivity(), "Notification", Toast.LENGTH_SHORT).show();
    }

    private static JSONObject convertBundleToJson(Bundle extras)
    {
        try
        {
            JSONObject json;
            json = new JSONObject().put("event", "message");

            JSONObject jsondata = new JSONObject();
            Iterator<String> it = extras.keySet().iterator();
            while (it.hasNext())
            {
                String key = it.next();
                Object value = extras.get(key);

                // System data from Android
                if (key.equals("from") || key.equals("collapse_key"))
                {
                    json.put(key, value);
                }
                else if (key.equals("foreground"))
                {
                    json.put(key, extras.getBoolean("foreground"));
                }
                else if (key.equals("coldstart"))
                {
                    json.put(key, extras.getBoolean("coldstart"));
                }
                else
                {
                    // Maintain backwards compatibility
                    if (key.equals("message") || key.equals("msgcnt") || key.equals("soundname"))
                    {
                        json.put(key, value);
                    }

                    if ( value instanceof String ) {
                        // Try to figure out if the value is another JSON object

                        String strValue = (String)value;
                        if (strValue.startsWith("{")) {
                            try {
                                JSONObject json2 = new JSONObject(strValue);
                                jsondata.put(key, json2);
                            }
                            catch (Exception e) {
                                jsondata.put(key, value);
                            }
                            // Try to figure out if the value is another JSON array
                        }
                        else if (strValue.startsWith("["))
                        {
                            try
                            {
                                JSONArray json2 = new JSONArray(strValue);
                                jsondata.put(key, json2);
                            }
                            catch (Exception e)
                            {
                                jsondata.put(key, value);
                            }
                        }
                        else
                        {
                            jsondata.put(key, value);
                        }
                    }
                }
            } // while
            json.put("payload", jsondata);

            Log.v(TAG, "extrasToJSON: " + json.toString());

            return json;
        }
        catch( JSONException e)
        {
            Log.e(TAG, "extrasToJSON: JSON exception");
        }
        return null;
    }
}
