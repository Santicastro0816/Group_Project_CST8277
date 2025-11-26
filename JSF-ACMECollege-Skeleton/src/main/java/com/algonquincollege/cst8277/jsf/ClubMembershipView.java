package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;
import java.net.URI;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.jsf.LoginBean;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;
import com.algonquincollege.cst8277.utility.MyConstants;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Named("clubMembershipView")
@RequestScoped
public class ClubMembershipView implements Serializable, MyConstants {
    private static final long serialVersionUID = 1L;

    @Inject
    protected FacesContext facesContext;
    @Inject
    protected ExternalContext externalContext;
    @Inject
    protected ServletContext sc;
    @Inject
    protected LoginBean loginBean;

    protected Integer studentId;
    protected Integer clubId;

    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    @PostConstruct
    public void initialize() {
        uri = UriBuilder
                .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
                .scheme(HTTP_SCHEMA)
                .host(HOST)
                .port(PORT)
                .build();
        
        auth = HttpAuthenticationFeature.basic(loginBean.getUsername(), loginBean.getPassword());
        
        client = ClientBuilder.newClient(
                new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        
        webTarget = client.target(uri);
    }

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

        try {
            Response response = webTarget
                    .register(auth)
                    .path(STUDENT_CLUB_RESOURCE_NAME + "/" + clubId + "/members/" + studentId)
                    .request()
                    .post(null);

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Student " + studentId + " successfully added to Club " + clubId, null));
                clear();
            } else {
                String errorMsg = response.readEntity(String.class);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Failed to add student to club. Status: " + response.getStatus() + " - " + errorMsg, null));
            }
        } catch (Exception e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error adding student to club: " + e.getMessage(), null));
        }
    }

    public String clear() {
        this.studentId = null;
        this.clubId = null;
        return null; // Stay on same page
    }
}
