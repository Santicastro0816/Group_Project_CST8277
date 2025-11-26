package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.CourseRegistration;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("assignGradeView")
@RequestScoped
public class AssignGradeView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    protected CourseRegistrationController courseRegistrationController;

    @Inject
    protected FacesContext facesContext;

    protected Integer studentId;
    protected Integer courseId;
    protected String letterGrade;

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

    public String getLetterGrade() {
        return letterGrade;
    }
    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public void submit() {
        if (studentId == null || courseId == null ||
            letterGrade == null || letterGrade.isBlank()) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Student Id, Course Id and Grade are required", null));
            return;
        }

        CourseRegistration registration =
                courseRegistrationController.findCourseRegistration(studentId, courseId);

        if (registration == null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No course registration found for given student and course", null));
            return;
        }

        registration.setLetterGrade(letterGrade);
        courseRegistrationController.updateCourseRegistration(registration);
        clear();
    }

    public void clear() {
        this.studentId = null;
        this.courseId = null;
        this.letterGrade = null;
    }
}
