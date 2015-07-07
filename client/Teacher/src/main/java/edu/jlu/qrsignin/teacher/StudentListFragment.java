package edu.jlu.qrsignin.teacher;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import edu.jlu.qrsignin.teacher.models.Result;
import edu.jlu.qrsignin.teacher.models.Student;
import edu.jlu.qrsignin.teacher.models.StudentList;

/**
 * @author xubowen92@gmail.com
 */
public class StudentListFragment extends Fragment {

  private RecyclerView listView;

  private StudentListAdapter adapter;

  private ProgressDialog progressDialog;

  private int courseId;

  public static StudentListFragment newInstance(int courseId) {
    StudentListFragment fragment = new StudentListFragment();
    fragment.courseId = courseId;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list, container, false);
    listView = (RecyclerView) view.findViewById(R.id.list);
    listView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    adapter = new StudentListAdapter();
    listView.setAdapter(adapter);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    refreshList();
  }

  private void refreshList() {
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<StudentList>(
        Const.API.STUDENT_LIST, StudentList.class, new Response.Listener<StudentList>() {
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
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
      }
    }));
    Map<String, String> params = new HashMap<>();
    params.put(Const.Param.COURSE_ID, String.valueOf(courseId));
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<StudentList>(
        Utils.createUrl(Const.API.COURSE_STUDENT_LIST, params), StudentList.class,
        new Response.Listener<StudentList>() {
          @Override
          public void onResponse(StudentList response) {
            if (response != null) {
              if (response.code == 0) {
                Set<Integer> choosedStduentIds = new HashSet<Integer>();
                for (Student courseStudent : response.data) {
                  choosedStduentIds.add(courseStudent.getId());
                }
                adapter.setData(choosedStduentIds);
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
  private void chooseStudent(int studentId) {
    progressDialog = ProgressDialog.show(getActivity(), "请等待", "正在添加...", true, false);

    Map<String, String> params = new HashMap<>();
    params.put(Const.Param.COURSE_ID, String.valueOf(courseId));
    params.put(Const.Param.STUDENT_ID, String.valueOf(studentId));
    QrApplication.getInstance().getRequestQueue().add(new GsonRequest<Result>(
        Utils.createUrl(Const.API.COURSE_STUDENT_ADD, params), Result.class,
        new Response.Listener<Result>() {
          @Override
          public void onResponse(Result response) {
            if (response != null) {
              Toast.makeText(getActivity(), response.data, Toast.LENGTH_LONG).show();
              if (response.code == 0) {
                refreshList();
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

  private class StudentListAdapter extends RecyclerView.Adapter<StudentItemHolder> {

    private List<Student> allStudentList;

    private Set<Integer> choseIdList;

    public void setData(List<Student> studentList) {
      this.allStudentList = studentList;
      notifyDataSetChanged();
    }

    public void setData(Set<Integer> chosedIdList) {
      this.choseIdList = chosedIdList;
      notifyDataSetChanged();
    }

    @Override
    public StudentItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_student_list, viewGroup, false);
      return new StudentItemHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentItemHolder viewHolder, final int i) {
      viewHolder.name.setText(allStudentList.get(i).getName());
      viewHolder.stuid.setText(allStudentList.get(i).getStuid());
      viewHolder.major.setText(allStudentList.get(i).getMajor());
      if (choseIdList != null && choseIdList.contains(allStudentList.get(i).getId())) {
        viewHolder.choose.setEnabled(false);
      } else {
        viewHolder.choose.setEnabled(true);
      }
      viewHolder.choose.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          chooseStudent(allStudentList.get(i).getId());
        }
      });
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

    private Button choose;

    public StudentItemHolder(View itemView) {
      super(itemView);
      name = (TextView) itemView.findViewById(R.id.name);
      stuid = (TextView) itemView.findViewById(R.id.stuid);
      major = (TextView) itemView.findViewById(R.id.major);
      choose = (Button) itemView.findViewById(R.id.choose);
    }
  }
}
