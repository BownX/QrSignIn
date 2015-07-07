package edu.jlu.qrsignin.teacher;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xubowen92@gmail.com
 */
public class LessonQrFragment extends Fragment {
  private ImageView qrView;

  private int lessonId;

  public static LessonQrFragment newInstance(int lessonId) {
    LessonQrFragment fragment = new LessonQrFragment();
    fragment.lessonId = lessonId;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_lesson_qr, container, false);
    qrView = (ImageView) view.findViewById(R.id.qr_view);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    new AsyncTask<Void, Void, Bitmap>() {

      @Override
      protected Bitmap doInBackground(Void... params) {
        Map<String, String> param = new HashMap<String, String>();
        param.put(Const.Param.LESSON_ID, String.valueOf(lessonId));
        return Utils.generateQRCode(Utils.createUrl(Const.API.SIGN_IN, param));
      }

      @Override
      protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
          qrView.setImageBitmap(bitmap);
        }
      }
    }.execute();
  }
}
