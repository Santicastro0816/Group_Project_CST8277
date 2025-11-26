package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.CourseRegistrationPK;
import com.algonquincollege.cst8277.entity.Student;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("courseRegistrationView")
@RequestScoped
public class CourseRegistrationView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    protected CourseRegistrationController courseRegistrationController;

    @Inject
    protected FacesContext facesContext;

    protected Integer studentId;
    protected Integer courseId;
    protected Integer year;
    protected String semester;

    public Integer getStudentId() {
        return studentId;
    }
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getCourseId() {
        return courseId;
    }
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }
    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void submit() {
        if (studentId == null || courseId == null || year == null ||
            semester == null || semester.isBlank()) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "All fields are required for course registration", null));
            return;
        }

        CourseRegistration registration = new CourseRegistration();
        CourseRegistrationPK pk = new CourseRegistrationPK();
        pk.setStudentId(studentId);
        pk.setCourseId(courseId);
        registration.setId(pk);

        registration.setYear(year);
        registration.setSemester(semester);

        Student s = new Student();
        s.setId(studentId);
        registration.setStudent(s);

        Course c = new Course();
        c.setId(courseId);
        registration.setCourse(c);

        courseRegistrationController.createCourseRegistration(registration);
        clear();
    }

    public void clear() {
        this.studentId = null;
        this.courseId = null;
        this.year = null;
        this.semester = null;
    }
}
