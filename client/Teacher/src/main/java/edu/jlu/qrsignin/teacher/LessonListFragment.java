package edu.jlu.qrsignin.teacher;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.jlu.qrsignin.teacher.models.Lesson;
import edu.jlu.qrsignin.teacher.models.LessonList;
import edu.jlu.qrsignin.teacher.models.Result;

/**
 * @author xubowen92@gmail.com
 */
public class LessonListFragment extends Fragment {

  private EditText lessonInfoView;

  private Button lessonAddView;

  private RecyclerView lessonListView;

  private LessonListAdapter adapter;

  private ProgressDialog progressDialog;

  private int courseId;

  public static LessonListFragment newInstance(int courseId) {
    LessonListFragment fragment = new LessonListFragment();
    fragment.courseId = courseId;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);
    lessonInfoView = (EditText) view.findViewById(R.id.lesson_info);
    lessonAddView = (Button) view.findViewById(R.id.lesson_add);
    lessonListView = (RecyclerView) view.findViewById(R.id.lesson_list);
    lessonListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    adapter = new LessonListAdapter();
    lessonListView.setAdapter(adapter);
    lessonAddView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doLessonAdd();
      }
    });
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    refreshList();
  }

  private void doLessonAdd() {
    String lessonInfo = lessonInfoView.getText().toString();

    if (TextUtils.isEmpty(lessonInfo)) {
      Toast.makeText(getActivity(), "请补全参数", Toast.LENGTH_LONG).show();
      return;
    }

    progressDialog = ProgressDialog.show(getActivity(), "请等待", "正在提交...", true, false);
    Map<String, String> params = new HashMap<>();
    params.put(Const.Param.INFO, lessonInfo);
    params.put(Const.Param.COURSE_ID, String.valueOf(courseId));
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<Result>(
        Utils.createUrl(Const.API.LESSON_ADD, params), Result.class, new Response.Listener<Result>() {
      @Override
      public void onResponse(Result response) {
        if (response != null) {
          Toast.makeText(getActivity(), response.data, Toast.LENGTH_LONG).show();
          if (response.code == 0) {
            refreshList();
            lessonInfoView.setText("");
          }
        }
        if (progressDialog != null) {
          progressDialog.dismiss();
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        if (progressDialog != null) {
          progressDialog.dismiss();
        }
      }
    }));
  }

  private void refreshList() {
    Map<String, String> params = new HashMap<>();
    params.put(Const.Param.COURSE_ID, String.valueOf(courseId));
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<LessonList>(
        Utils.createUrl(Const.API.LESSON_LIST, params), LessonList.class, new Response.Listener<LessonList>() {
      @Override
      public void onResponse(LessonList response) {
        if (response != null) {
          if (response.code == 0) {
            adapter.setData(response.data);
          } else {
            Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_LONG).show();
          }
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
      }
    }));
  }

  private class LessonListAdapter extends RecyclerView.Adapter<LessonItemHolder> {

    private List<Lesson> lessonList;

    public void setData(List<Lesson> lessonList) {
      this.lessonList = lessonList;
      notifyDataSetChanged();
    }

    @Override
    public LessonItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_lesson_list, viewGroup, false);
      return new LessonItemHolder(view);
    }

    @Override
    public void onBindViewHolder(LessonItemHolder viewHolder, final int i) {
      viewHolder.info.setText(lessonList.get(i).getInfo());
      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          LessonInfoActivity.launch(getActivity(), courseId, lessonList.get(i).getId(),
              lessonList.get(i).getInfo());
        }
      });
    }

    @Override
    public int getItemCount() {
      return lessonList != null ? lessonList.size() : 0;
    }
  }

  private class LessonItemHolder extends RecyclerView.ViewHolder {

    private TextView info;

    public LessonItemHolder(View itemView) {
      super(itemView);
      info = (TextView) itemView.findViewById(R.id.info);
    }
  }
}
