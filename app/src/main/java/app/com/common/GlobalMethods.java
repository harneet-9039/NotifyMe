package app.com.common;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import android.app.ProgressDialog;
import org.json.JSONArray;

public class GlobalMethods {
    static String URL = "https://app--notifyme.herokuapp.com/";
    //static String URL = "http://192.168.43.137:3000/";



    public static String GetSubString(String res)
    {
        return res.substring(13,16);

    }
    public static void print(Context context, String msg)
    {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    public static String getURL()
    {
        return URL;
    }


    }
