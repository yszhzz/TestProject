package com.mycode.entity;

import org.openqa.selenium.WebElement;

import java.util.List;

public class Course {

    private int course_index;
    private String course_title;
    private WebElement course;
    private boolean study_over = false;
    private String percent;
    private int task_num = 0;
    private List<WebElement> tasks;


    public Course(int course_index, String course_title, WebElement course) {
        this.course_index = course_index;
        this.course_title = course_title;
        this.course = course;
    }

    public int getCourse_index() {
        return course_index;
    }

    public void setCourse_index(int course_index) {
        this.course_index = course_index;
    }

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public WebElement getCourse() {
        return course;
    }

    public void setCourse(WebElement course) {
        this.course = course;
    }

    public boolean isStudy_over() {
        return study_over;
    }

    public void setStudy_over(boolean study_over) {
        this.study_over = study_over;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public int getTask_num() {
        return task_num;
    }

    public void setTask_num(int task_num) {
        this.task_num = task_num;
    }

    public List<WebElement> getTasks() {
        return tasks;
    }

    public void setTasks(List<WebElement> tasks) {
        this.tasks = tasks;
    }

/*    @Override
    public String toString() {
        return "Course{" +
                "course_title='" + course_title + '\'' +
                '}';
    }*/

    @Override
    public String toString() {
        return "Course{" +
                "course_index=" + course_index +
                ", course_title='" + course_title + '\'' +
                ", course=" + course +
                ", study_over=" + study_over +
                ", percent='" + percent + '\'' +
                ", task_num=" + task_num +
                ", tasks=" + tasks +
                '}';
    }
}
