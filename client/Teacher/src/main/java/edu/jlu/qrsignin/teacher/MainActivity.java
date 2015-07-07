package edu.jlu.qrsignin.teacher;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_frame);
    getSupportActionBar().setTitle("所有科目");
    getSupportFragmentManager().beginTransaction().replace(R.id.content,
        new CourseListFragment()).commit();
  }
}
