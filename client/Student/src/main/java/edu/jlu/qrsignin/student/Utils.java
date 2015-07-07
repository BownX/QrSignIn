package edu.jlu.qrsignin.student;

import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;

import java.util.Map;

/**
 * @author xubowen92@gmail.com
 */
public class Utils {

  public static String getDeviceId(Context context) {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (tm != null) {
      return tm.getDeviceId();
    }
    return "";
  }

  public static String createUrl(String url, Map<String, String> params) {
    Uri uri = Uri.parse(url);
    Uri.Builder builder = uri.buildUpon();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      builder.appendQueryParameter(entry.getKey(), entry.getValue());
    }
    return builder.build().toString();
  }

}
