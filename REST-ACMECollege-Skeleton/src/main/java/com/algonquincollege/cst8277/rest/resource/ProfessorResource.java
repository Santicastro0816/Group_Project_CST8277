package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.PROFESSOR_RESOURCE_NAME;
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
import com.algonquincollege.cst8277.entity.Professor;

@Path(PROFESSOR_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getProfessors() {
        LOG.debug("Retrieving all professors...");
        List<Professor> professors = service.getAllProfessors();
        return Response.ok(professors).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Retrieving professor with id = {}", id);
        Professor professor = service.getProfessorById(id);
        if (professor == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(professor).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
        LOG.debug("Adding a new professor = {}", newProfessor);
        Professor persistedProfessor = service.persistProfessor(newProfessor);
        return Response.ok(persistedProfessor).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Professor updatingProfessor) {
        LOG.debug("Updating professor with id = {}", id);
        Professor updatedProfessor = service.updateProfessorById(id, updatingProfessor);
        if (updatedProfessor == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(updatedProfessor).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting professor with id = {}", id);
        Professor deletedProfessor = service.deleteProfessorById(id);
        if (deletedProfessor == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(deletedProfessor).build();
    }
}