package edu.jlu.qrsignin.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author xubowen92@gmail.com
 */
public class LessonInfoActivity extends ActionBarActivity {

  private static final String EXTRA_COURSE_ID = "extra.course.id";

  private static final String EXTRA_LESSON_ID = "extra.lesson.id";

  private static final String EXTRA_LESSON_INFO = "extra.lesson.info";

  private int lessonId;

  private int courseId;

  private String info;

  public static void launch(Context context, int courseId, int lessonId, String info) {
    Intent intent = new Intent(context, LessonInfoActivity.class);
    intent.putExtra(EXTRA_LESSON_ID, lessonId);
    intent.putExtra(EXTRA_COURSE_ID, courseId);
    intent.putExtra(EXTRA_LESSON_INFO, info);
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_frame);
    getSupportActionBar().setTitle("课程信息");

    Intent intent = getIntent();
    if (intent != null) {
      lessonId = intent.getIntExtra(EXTRA_LESSON_ID, -1);
      courseId = intent.getIntExtra(EXTRA_COURSE_ID, -1);
      info = intent.getStringExtra(EXTRA_LESSON_INFO);
    }

    if (TextUtils.isEmpty(info)) {
      getSupportActionBar().setTitle(info);
    }

    getSupportFragmentManager().beginTransaction().replace(R.id.content,
        LessonQrFragment.newInstance(lessonId)).commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_lesson_info, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_qr:
        getSupportFragmentManager().beginTransaction().replace(R.id.content,
            LessonQrFragment.newInstance(lessonId)).commit();
        break;
      case R.id.action_signin_list:
        getSupportFragmentManager().beginTransaction().replace(R.id.content,
            LessonSignInFragment.newInstance(courseId, lessonId)).commit();
        break;
    }
    return true;
  }
}
