package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.Course;

@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getCourses() {
        LOG.debug("Retrieving all courses...");
        List<Course> courses = service.getAllCourses();
        return Response.ok(courses).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Retrieving course with id = {}", id);
        Course course = service.getCourseById(id);
        if (course == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(course).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course newCourse) {
        LOG.debug("Adding a new course = {}", newCourse);
        Course persistedCourse = service.persistCourse(newCourse);
        return Response.ok(persistedCourse).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Course updatingCourse) {
        LOG.debug("Updating course with id = {}", id);
        Course updatedCourse = service.updateCourseById(id, updatingCourse);
        if (updatedCourse == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(updatedCourse).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting course with id = {}", id);
        Course deletedCourse = service.deleteCourseById(id);
        if (deletedCourse == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(deletedCourse).build();
    }
}