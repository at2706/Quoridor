package edu.nyu.at2706.quoridor;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class QuoridorTurn {
    public static final String TAG = "QuoridorTurn";
    public String action = "";
    public int x, y;
    public int turnCounter;

    public QuoridorTurn() {
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();

        try {
            retVal.put("action", action);
            retVal.put("x", x);
            retVal.put("y", y);
            retVal.put("turnCounter", turnCounter);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of SkeletonTurn.
    static public QuoridorTurn unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new QuoridorTurn();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        QuoridorTurn retVal = new QuoridorTurn();

        try {
            JSONObject obj = new JSONObject(st);

            if (obj.has("action")) {
                retVal.action = obj.getString("action");
            }
            if (obj.has("x")) {
                retVal.x = obj.getInt("x");
            }
            if (obj.has("y")) {
                retVal.y = obj.getInt("y");
            }
            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retVal;
    }
}
