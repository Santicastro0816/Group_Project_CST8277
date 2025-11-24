package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.algonquincollege.cst8277.entity.Course;

@Named("newCourse")
@RequestScoped
public class NewCourseView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String courseCode;
    protected String courseTitle;
    protected Integer creditUnits;
    protected Short online;

    @Inject
    protected CourseController courseController;

    public NewCourseView() {
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Integer getCreditUnits() {
        return creditUnits;
    }

    public void setCreditUnits(Integer creditUnits) {
        this.creditUnits = creditUnits;
    }

    public Short getOnline() {
        return online;
    }

    public void setOnline(Short online) {
        this.online = online;
    }

    public void addCourse() {
        if (allNotNull()) {
            Course newCourse = new Course();
            newCourse.setCourseCode(getCourseCode());
            newCourse.setCourseTitle(getCourseTitle());
            newCourse.setCreditUnits(getCreditUnits());
            newCourse.setOnline(getOnline());
            courseController.addNewCourse(newCourse);
            courseController.toggleAdding();
            
            // Reset form fields
            setCourseCode(null);
            setCourseTitle(null);
            setCreditUnits(null);
            setOnline(null);
        }
    }

    public boolean allNotNull() {
        return courseCode != null && courseTitle != null && creditUnits != null && online != null;
    }
}