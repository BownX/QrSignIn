package edu.jlu.qrsignin.teacher;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author xubowen92@gmail.com
 */
public class Const {

  private static SharedPreferences preference;

  public static final String PREF_NAME = "qrsignin";

  public static final String HOST_ADDR = "http://192.168.1.106:9090";

  public static void init(Context context) {
    preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
  }

  public static final class API {

    public static final String SIGN_IN = HOST_ADDR + "/signin";

    public static final String COURSE_ADD = HOST_ADDR + "/course/add";

    public static final String LESSON_ADD = HOST_ADDR + "/lesson/add";

    public static final String LESSON_LIST = HOST_ADDR + "/lesson/list";

    public static final String COURSE_LIST = HOST_ADDR + "/course/list";

    public static final String STUDENT_LIST = HOST_ADDR + "/student/list";

    public static final String COURSE_STUDENT_ADD = HOST_ADDR + "/course/student/add";

    public static final String COURSE_STUDENT_LIST = HOST_ADDR + "/course/student/list";

    public static final String LESSON_STUDENT_LIST = HOST_ADDR + "/lesson/student/list";

  }

  public static final class Param {

    public static final String IMEI = "imei";

    public static final String LESSON_ID = "lessonid";

    public static final String NAME = "name";

    public static final String STU_ID = "stuid";

    public static final String MAJOR = "major";

    public static final String TITLE = "title";

    public static final String TEACHER = "teacher";

    public static final String COURSE_ID = "courseid";

    public static final String INFO = "info";

    public static final String STUDENT_ID= "studentid";

  }

}
