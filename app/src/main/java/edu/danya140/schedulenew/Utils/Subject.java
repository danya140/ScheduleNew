package edu.danya140.schedulenew.Utils;

/**
 * Created by Данил on 29.08.2017.
 */
public class Subject {

    private String classroom;
    private String time;
    private String type;
    private String name;
    private String teacher;

    public void make(String clroom, String tim, String tp, String nam, String teach){
        classroom=clroom;
        time = tim;
        type = tp;
        name = nam;
        teacher = teach;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    private void normalize(){

    }
}
