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
public class CourseInfoActivity extends ActionBarActivity {

  public static final String EXTRA_COURSE_ID = "extra.course.id";

  public static final String EXTRA_COURSE_TITLE = "extra.course.title";

  private int courseId;

  private String courseTitle;

  public static void launch(Context context, int courseId, String courseTitle) {
    Intent intent = new Intent(context, CourseInfoActivity.class);
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    intent.putExtra(EXTRA_COURSE_ID, courseId);
    intent.putExtra(EXTRA_COURSE_TITLE, courseTitle);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_frame);
    getSupportActionBar().setTitle("学科信息");

    Intent intent = getIntent();
    if (intent != null) {
      courseId = intent.getIntExtra(EXTRA_COURSE_ID, -1);
      courseTitle = intent.getStringExtra(EXTRA_COURSE_TITLE);
    }

    if (!TextUtils.isEmpty(courseTitle)) {
      getSupportActionBar().setTitle("课程列表: " + courseTitle);
    }

    getSupportFragmentManager().beginTransaction().replace(R.id.content,
        LessonListFragment.newInstance(courseId)).commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_course_info, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_lesson_list:
        getSupportFragmentManager().beginTransaction().replace(R.id.content,
            LessonListFragment.newInstance(courseId)).commit();
        if (!TextUtils.isEmpty(courseTitle)) {
          getSupportActionBar().setTitle("课程列表: " + courseTitle);
        }
        break;
      case R.id.action_student_list:
        getSupportFragmentManager().beginTransaction().replace(R.id.content,
            StudentListFragment.newInstance(courseId)).commit();
        if (!TextUtils.isEmpty(courseTitle)) {
          getSupportActionBar().setTitle("学生列表：" + courseTitle);
        }
        break;
    }
    return true;
  }

}
