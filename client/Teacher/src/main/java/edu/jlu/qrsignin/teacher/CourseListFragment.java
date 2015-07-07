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

import edu.jlu.qrsignin.teacher.models.Course;
import edu.jlu.qrsignin.teacher.models.CourseList;
import edu.jlu.qrsignin.teacher.models.Result;

/**
 * @author xubowen92@gmail.com
 */
public class CourseListFragment extends Fragment {

  private ProgressDialog progressDialog;
  private RecyclerView recyclerView;
  private EditText courseTitleView;
  private EditText courseTeacherView;
  private Button courseAddButton;
  private CourseListAdapter adapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_course_list, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.course_list);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    adapter = new CourseListAdapter();
    recyclerView.setAdapter(adapter);
    courseTitleView = (EditText) view.findViewById(R.id.course_title);
    courseTeacherView = (EditText) view.findViewById(R.id.course_teacher);
    courseAddButton = (Button) view.findViewById(R.id.course_add);
    courseAddButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doAddCourse();
      }
    });
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    refreshList();
  }

  private void doAddCourse() {
    String courseTitle = courseTitleView.getText().toString();
    String courseTeacher = courseTeacherView.getText().toString();
    if (TextUtils.isEmpty(courseTitle) || TextUtils.isEmpty(courseTeacher)) {
      Toast.makeText(getActivity(), "请补全参数", Toast.LENGTH_LONG).show();
      return;
    }

    progressDialog = ProgressDialog.show(getActivity(), "请等待", "正在提交...", true, false);
    Map<String, String> params = new HashMap<>();
    params.put(Const.Param.TITLE, courseTitle);
    params.put(Const.Param.TEACHER, courseTeacher);
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<Result>(
        Utils.createUrl(Const.API.COURSE_ADD, params), Result.class, new Response.Listener<Result>() {
      @Override
      public void onResponse(Result response) {
        if (response != null) {
          Toast.makeText(getActivity(), response.data, Toast.LENGTH_LONG).show();
          if (response.code == 0) {
            refreshList();
            courseTeacherView.setText("");
            courseTitleView.setText("");
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
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<CourseList>(
        Const.API.COURSE_LIST, CourseList.class, new Response.Listener<CourseList>() {
      @Override
      public void onResponse(CourseList response) {
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

  private class CourseListAdapter extends RecyclerView.Adapter<CourseItemHolder> {

    private List<Course> courseList;

    public void setData(List<Course> courseList) {
      this.courseList = courseList;
      notifyDataSetChanged();
    }

    @Override
    public CourseItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_course_list, viewGroup, false);
      return new CourseItemHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseItemHolder viewHolder, final int i) {
      viewHolder.title.setText(courseList.get(i).getTitle());
      viewHolder.teacher.setText(courseList.get(i).getTeacher());
      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          CourseInfoActivity.launch(getActivity(), courseList.get(i).getId(), courseList.get(i).getTitle());
        }
      });
    }

    @Override
    public int getItemCount() {
      return courseList != null ? courseList.size() : 0;
    }
  }

  private class CourseItemHolder extends RecyclerView.ViewHolder {

    private TextView title;

    private TextView teacher;

    public CourseItemHolder(View itemView) {
      super(itemView);
      title = (TextView) itemView.findViewById(R.id.title);
      teacher = (TextView) itemView.findViewById(R.id.teacher);
    }
  }
}
