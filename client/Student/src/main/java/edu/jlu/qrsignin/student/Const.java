package edu.jlu.qrsignin.student;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author xubowen92@gmail.com
 */
public class Const {

  private static SharedPreferences preference;

  public static final String PREF_NAME = "qrsignin";

  public static final String PREF_REGISTERED = "registered";

  public static final String HOST_ADDR = "http://qrsignin.bown.xyz";

  public static final String API_SIGNIN = HOST_ADDR + "/signin";

  public static final String API_REGISTER = HOST_ADDR + "/student/register";

  public static void init(Context context) {
    preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
  }

  public static boolean isRegistered() {
    return preference.getBoolean(PREF_REGISTERED, false);
  }
  
  public static void setRegistered(boolean registered) {
    SharedPreferences.Editor editor = preference.edit();
    editor.putBoolean(PREF_REGISTERED, registered);
    editor.commit();
  }

  public static final class Param {

    public static final String IMEI = "imei";

    public static final String LESSON_ID = "lessonid";

    public static final String NAME = "name";

    public static final String STU_ID = "stuid";

    public static final String MAJOR = "major";

  }

}
