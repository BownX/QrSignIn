package edu.jlu.qrsignin.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xubowen92@gmail.com
 */
public class SignInFragment extends Fragment {

  private ProgressDialog progressDialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
    view.findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ScanActivity.launchForResult(SignInFragment.this);
      }
    });
    return view;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ScanActivity.REQ_CODE_CAPTURE && data != null) {
      String result = data.getStringExtra(ScanActivity.EXTRA_RESULT);
      if (result.startsWith(Const.API_SIGNIN)) {
        doSignIn(result);
      }
    }
  }

  private void doSignIn(String url) {
    Map<String, String> params = new HashMap<>();
    params.put(Const.Param.IMEI, Utils.getDeviceId(getActivity()));
    progressDialog = ProgressDialog.show(getActivity(),"请等待","正在签到中...",true,false);
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<Result>(
        Utils.createUrl(url, params), Result.class, new Response.Listener<Result>() {
      @Override
      public void onResponse(Result response) {
        if (progressDialog != null) {
          progressDialog.dismiss();
        }
        if (response != null) {
          Toast.makeText(getActivity(), response.data, Toast.LENGTH_SHORT).show();
          if (response.code == 0) {
            getActivity().finish();
          }
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
        if (progressDialog != null) {
          progressDialog.dismiss();
        }
      }
    }));
  }
}
