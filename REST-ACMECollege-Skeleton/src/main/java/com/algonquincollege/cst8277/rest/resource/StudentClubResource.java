package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
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
import com.algonquincollege.cst8277.entity.StudentClub;

@Path(STUDENT_CLUB_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentClubResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getStudentClubs() {
        LOG.debug("Retrieving all student clubs...");
        List<StudentClub> studentClubs = service.getAllStudentClubs();
        return Response.ok(studentClubs).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Retrieving student club with id = {}", id);
        StudentClub studentClub = service.getStudentClubById(id);
        if (studentClub == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(studentClub).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addStudentClub(StudentClub newStudentClub) {
        LOG.debug("Adding a new student club = {}", newStudentClub);
        StudentClub persistedStudentClub = service.persistStudentClub(newStudentClub);
        return Response.ok(persistedStudentClub).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, StudentClub updatingStudentClub) {
        LOG.debug("Updating student club with id = {}", id);
        StudentClub updatedStudentClub = service.updateStudentClubById(id, updatingStudentClub);
        if (updatedStudentClub == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(updatedStudentClub).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting student club with id = {}", id);
        StudentClub deletedStudentClub = service.deleteStudentClubById(id);
        if (deletedStudentClub == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(deletedStudentClub).build();
    }
}