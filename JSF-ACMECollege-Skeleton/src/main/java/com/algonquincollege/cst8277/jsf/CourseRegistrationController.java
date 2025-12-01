package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;
import java.net.URI;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.CourseRegistrationPK;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;
import com.algonquincollege.cst8277.utility.MyConstants;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Named("courseRegistrationController")
@SessionScoped
public class CourseRegistrationController implements Serializable, MyConstants {
    private static final long serialVersionUID = 1L;

    @Inject
    protected FacesContext facesContext;
    @Inject
    protected ExternalContext externalContext;
    @Inject
    protected ServletContext sc;
    @Inject
    protected LoginBean loginBean;

    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public CourseRegistrationController() {
        super();
    }

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
        // No auto-load; these forms just call create/update on demand.
    }

    public CourseRegistration createCourseRegistration(CourseRegistration registration) {
        try {
            Response response = webTarget
                    .register(auth)
                    .path(COURSE_REGISTRATION_RESOURCE_NAME)
                    .request()
                    .post(Entity.json(registration));

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                CourseRegistration created = response.readEntity(CourseRegistration.class);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Course registration created successfully", null));
                return created;
            }
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Failed to create course registration. Status: " + response.getStatus(), null));
        }
        catch (Exception e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error creating course registration: " + e.getMessage(), null));
        }
        return null;
    }

    public CourseRegistration findCourseRegistration(int studentId, int courseId) {
        try {
            // Expected REST path: /courseregistration/{studentId}/{courseId}
            Response response = webTarget
                    .register(auth)
                    .path(COURSE_REGISTRATION_RESOURCE_NAME + SLASH + studentId + SLASH + courseId)
                    .request()
                    .get();

            if (response.getStatus() == 200) {
                return response.readEntity(CourseRegistration.class);
            }
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Course registration not found. Status: " + response.getStatus(), null));
        }
        catch (Exception e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error loading course registration: " + e.getMessage(), null));
        }
        return null;
    }

    public CourseRegistration updateCourseRegistration(CourseRegistration registrationWithUpdates) {
        if (registrationWithUpdates == null || registrationWithUpdates.getId() == null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Course Registration is missing required id values", null));
            return null;
        }

        CourseRegistrationPK pk = registrationWithUpdates.getId();

        try {
            Response response = webTarget
                    .register(auth)
                    .path(COURSE_REGISTRATION_RESOURCE_NAME + SLASH +
                          pk.getStudentId() + SLASH + pk.getCourseId())
                    .request()
                    .put(Entity.json(registrationWithUpdates));

            if (response.getStatus() == 200) {
                CourseRegistration updated = response.readEntity(CourseRegistration.class);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Course registration updated successfully", null));
                return updated;
            }
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Failed to update course registration. Status: " + response.getStatus(), null));
        }
        catch (Exception e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error updating course registration: " + e.getMessage(), null));
        }
        return null;
    }

    public CourseRegistration assignProfessorToCourseRegistration(int studentId, int courseId, int professorId) {
        try {
            // Create a minimal Professor object with just the ID
            com.algonquincollege.cst8277.entity.Professor professor = new com.algonquincollege.cst8277.entity.Professor();
            professor.setId(professorId);
            
            Response response = webTarget
                    .register(auth)
                    .path(COURSE_REGISTRATION_RESOURCE_NAME + SLASH + "student" + SLASH + studentId + SLASH + "course" + SLASH + courseId)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(professor, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 200) {
                CourseRegistration updated = response.readEntity(CourseRegistration.class);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Professor assigned successfully", null));
                return updated;
            }
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Failed to assign professor. Status: " + response.getStatus(), null));
        }
        catch (Exception e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error assigning professor: " + e.getMessage(), null));
        }
        return null;
    }

    public CourseRegistration assignGradeToCourseRegistration(int studentId, int courseId, String letterGrade) {
        try {
            Response response = webTarget
                    .register(auth)
                    .path(COURSE_REGISTRATION_RESOURCE_NAME + SLASH + "student" + SLASH + studentId + SLASH + "course" + SLASH + courseId)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(letterGrade, MediaType.TEXT_PLAIN));

            if (response.getStatus() == 200) {
                CourseRegistration updated = response.readEntity(CourseRegistration.class);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Grade assigned successfully", null));
                return updated;
            }
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Failed to assign grade. Status: " + response.getStatus(), null));
        }
        catch (Exception e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error assigning grade: " + e.getMessage(), null));
        }
        return null;
    }
}