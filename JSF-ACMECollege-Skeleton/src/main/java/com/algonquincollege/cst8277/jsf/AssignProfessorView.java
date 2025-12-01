package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;


import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("assignProfessorView")
@RequestScoped
public class AssignProfessorView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    protected CourseRegistrationController courseRegistrationController;

    @Inject
    protected FacesContext facesContext;

    protected Integer studentId;
    protected Integer courseId;
    protected Integer professorId;

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

    public Integer getProfessorId() {
        return professorId;
    }
    public void setProfessorId(Integer professorId) {
        this.professorId = professorId;
    }

    public void submit() {
        if (studentId == null || courseId == null || professorId == null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Student Id, Course Id and Professor Id are required", null));
            return;
        }

        courseRegistrationController.assignProfessorToCourseRegistration(studentId, courseId, professorId);
        clear();
    }

    public String clear() {
        this.studentId = null;
        this.courseId = null;
        this.professorId = null;
        return null; // Stay on same page
    }
}