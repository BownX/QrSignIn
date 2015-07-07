package edu.jlu.qrsignin.student;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements RegisterFragment.RegisterListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_frame);

    Fragment fragment;
    if (Const.isRegistered()) {
      fragment = new SignInFragment();
    } else {
      fragment = new RegisterFragment();
    }
    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.action_quit:
        finish();
        break;
    }
    return true;
  }

  @Override
  public void finishRegister() {
    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
        new SignInFragment()).commit();
  }
}
