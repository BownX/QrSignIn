package edu.jlu.qrsignin.student;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xubowen92@gmail.com
 */
public class RegisterFragment extends Fragment {

  private EditText nameView;

  private EditText stuIdView;

  private EditText majorView;

  private Button registerButton;

  private RegisterListener listener;

  private ProgressDialog progressDialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof RegisterListener) {
      listener = (RegisterListener) activity;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_register, container, false);
    nameView = (EditText) view.findViewById(R.id.name);
    stuIdView = (EditText) view.findViewById(R.id.stuid);
    majorView = (EditText) view.findViewById(R.id.major);
    registerButton = (Button) view.findViewById(R.id.register);
    registerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doRegister();
      }
    });
    return view;
  }

  private void doRegister() {
    String name = nameView.getText().toString();
    String stuid = stuIdView.getText().toString();
    String major = majorView.getText().toString();

    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(stuid) || TextUtils.isEmpty(major)) {
      Toast.makeText(getActivity(), "请补全参数", Toast.LENGTH_SHORT).show();
      return;
    }

    Map<String, String> params = new HashMap<>();
    params.put(Const.Param.NAME, name);
    params.put(Const.Param.STU_ID, stuid);
    params.put(Const.Param.MAJOR, major);
    params.put(Const.Param.IMEI, Utils.getDeviceId(getActivity()));
    progressDialog = ProgressDialog.show(getActivity(), "请等待", "正在注册中...", true, false);
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<Result>(
        Utils.createUrl(Const.API_REGISTER, params),
        Result.class, new Response.Listener<Result>() {
      @Override
      public void onResponse(Result response) {
        if (progressDialog != null) {
          progressDialog.dismiss();
        }
        if (response != null) {
          Toast.makeText(getActivity(), response.data, Toast.LENGTH_SHORT).show();
          if (response.code == 0 || response.code == 2) {
            Const.setRegistered(true);
            if (listener != null) {
              listener.finishRegister();
            }
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

  public interface RegisterListener {

    void finishRegister();

  }

}
