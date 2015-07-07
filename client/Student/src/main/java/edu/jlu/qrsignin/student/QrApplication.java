package edu.jlu.qrsignin.student;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * @author xubowen92@gmail.com
 */
public class QrApplication extends Application {

  private static QrApplication instance;

  private RequestQueue requestQueue;

  @Override
  public void onCreate() {
    super.onCreate();
    Const.init(this);
    requestQueue = Volley.newRequestQueue(this);
    instance = (QrApplication) getApplicationContext();
  }

  public static QrApplication getInstance() {
    return instance;
  }

  public RequestQueue getRequestQueue() {
    return requestQueue;
  }
}
