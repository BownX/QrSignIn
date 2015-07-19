package edu.jlu.qrsignin.teacher.models;

/**
 * @author xubowen92@gmail.com
 */
public class SignIn {

  private String id;

  private String signtime;

  private Student student;

  private Lesson lesson;

  public String getId() {
    return id;
  }

  public String getSigntime() {
    return signtime;
  }

  public Student getStudent() {
    return student;
  }

  public Lesson getLesson() {
    return lesson;
  }
}
