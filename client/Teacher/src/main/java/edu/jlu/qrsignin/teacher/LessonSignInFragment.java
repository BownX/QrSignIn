package edu.jlu.qrsignin.teacher;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.jlu.qrsignin.teacher.models.SignIn;
import edu.jlu.qrsignin.teacher.models.SignInList;
import edu.jlu.qrsignin.teacher.models.Student;
import edu.jlu.qrsignin.teacher.models.StudentList;

/**
 * @author xubowen92@gmail.com
 */
public class LessonSignInFragment extends Fragment {

  private RecyclerView recyclerView;

  private StudentListAdapter adapter;

  private int lessonId;

  private int courseId;

  public static LessonSignInFragment newInstance(int courseId, int lessonId) {
    LessonSignInFragment fragment = new LessonSignInFragment();
    fragment.lessonId = lessonId;
    fragment.courseId = courseId;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.list);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    adapter = new StudentListAdapter();
    recyclerView.setAdapter(adapter);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    refreshList();
  }

  private void refreshList() {
    Map<String, String> params = new HashMap<>();
    params.put(Const.Param.COURSE_ID, String.valueOf(courseId));
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<StudentList>(
        Utils.createUrl(Const.API.COURSE_STUDENT_LIST, params), StudentList.class,
        new Response.Listener<StudentList>() {
      @Override
      public void onResponse(StudentList response) {
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
        error.printStackTrace();
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
      }
    }));
    Map<String, String> paramSignIn = new HashMap<>();
    paramSignIn.put(Const.Param.LESSON_ID, String.valueOf(lessonId));
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<SignInList>(
        Utils.createUrl(Const.API.LESSON_STUDENT_LIST, paramSignIn), SignInList.class,
        new Response.Listener<SignInList>() {
          @Override
          public void onResponse(SignInList response) {
            if (response != null) {
              if (response.code == 0) {
                Set<Integer> signedIds  = new HashSet<Integer>();
                for (SignIn courseStudent : response.data) {
                  signedIds.add(courseStudent.getStudent().getId());
                }
                adapter.setData(signedIds);
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

  private class StudentListAdapter extends RecyclerView.Adapter<StudentItemHolder> {
    
    private List<Student> allStudentList;
    private Set<Integer> signedIdList;

    public void setData(List<Student> studentList) {
      this.allStudentList = studentList;
      notifyDataSetChanged();
    }

    public void setData(Set<Integer> signedIdList) {
      this.signedIdList = signedIdList;
      notifyDataSetChanged();
    }

    @Override
    public StudentItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_signin, viewGroup, false);
      return new StudentItemHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentItemHolder viewHolder, final int i) {
      viewHolder.name.setText(allStudentList.get(i).getName());
      viewHolder.stuid.setText(allStudentList.get(i).getStuid());
      viewHolder.major.setText(allStudentList.get(i).getMajor());
      if (signedIdList != null && signedIdList.contains(allStudentList.get(i).getId())) {
        viewHolder.status.setText("已签到");
        viewHolder.status.setBackgroundColor(Color.GREEN);
      } else {
        viewHolder.status.setText("未签到");
        viewHolder.status.setBackgroundColor(Color.RED);
      }
    }

    @Override
    public int getItemCount() {
      return allStudentList != null ? allStudentList.size() : 0;
    }
  }

  private class StudentItemHolder extends RecyclerView.ViewHolder {

    private TextView name;

    private TextView stuid;

    private TextView major;

    private TextView status;

    public StudentItemHolder(View itemView) {
      super(itemView);
      name = (TextView) itemView.findViewById(R.id.name);
      stuid = (TextView) itemView.findViewById(R.id.stuid);
      major = (TextView) itemView.findViewById(R.id.major);
      status = (TextView) itemView.findViewById(R.id.status);
    }
  }
}
