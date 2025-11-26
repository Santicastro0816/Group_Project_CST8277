package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("clubMembershipView")
@RequestScoped
public class ClubMembershipView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    protected FacesContext facesContext;

    protected Integer studentId;
    protected Integer clubId;

    public Integer getStudentId() {
        return studentId;
    }
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getClubId() {
        return clubId;
    }
    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    public void submit() {
        if (studentId == null || clubId == null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Student Id and Club Id are required", null));
            return;
        }

        // TODO: call your REST membership endpoint when implemented
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Club membership request captured for Student " +
                        studentId + " in Club " + clubId, null));

        clear();
    }

    public void clear() {
        this.studentId = null;
        this.clubId = null;
    }
}
