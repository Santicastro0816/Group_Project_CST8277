package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.StudentClub;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newStudentClub")
@ViewScoped
public class NewStudentClubView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected String desc;
    protected boolean academic;

    @Inject
    @ManagedProperty("#{studentClubController}")
    protected StudentClubController studentClubController;

    public NewStudentClubView() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isAcademic() {
        return academic;
    }

    public void setAcademic(boolean academic) {
        this.academic = academic;
    }

    public void addStudentClub() {
        // Create JSON with academic boolean (0 or 1)
        String json = String.format(
            "{\"name\":\"%s\",\"desc\":\"%s\",\"academic\":%s}",
            name != null ? name : "",
            desc != null ? desc : "",
            academic ? "true" : "false"
        );
        
        studentClubController.addNewStudentClubJson(json);
        
        // Clear the old list to force fresh reload
        studentClubController.setStudentClubs(new java.util.ArrayList<>());
        
        // Reload the student club list from server
        studentClubController.loadStudentClubs();
        
        //clean up
        studentClubController.toggleAdding();
        setName(null);
        setDesc(null);
        setAcademic(false);
    }

}