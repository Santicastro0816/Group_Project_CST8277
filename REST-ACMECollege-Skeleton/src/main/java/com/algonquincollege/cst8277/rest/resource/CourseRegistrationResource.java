package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import java.util.List;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.CourseRegistration;

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

@Path(COURSE_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseRegistrationResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    // GET /courseregistration
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllRegistrations() {
        LOG.debug("retrieving all course registrations ...");
        List<CourseRegistration> regs = service.getAllCourseRegistrations();
        return Response.ok(regs).build();
    }

    // GET /courseregistration/{studentId}/{courseId}
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{studentId}/{courseId}")
    public Response getRegistrationById(@PathParam("studentId") int studentId,
                                        @PathParam("courseId") int courseId) {
        LOG.debug("retrieving registration for student {} course {}", studentId, courseId);
        CourseRegistration reg = service.getCourseRegistrationById(studentId, courseId);
        if (reg == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(reg).build();
    }

    // POST /courseregistration
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response createRegistration(CourseRegistration newReg) {
        LOG.debug("creating new course registration");
        CourseRegistration created = service.persistCourseRegistration(newReg);
        return Response.status(Status.CREATED).entity(created).build();
    }

    // PUT /courseregistration/{studentId}/{courseId}
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{studentId}/{courseId}")
    public Response updateRegistration(@PathParam("studentId") int studentId,
                                       @PathParam("courseId") int courseId,
                                       CourseRegistration regWithUpdates) {
        LOG.debug("updating registration for student {} course {}", studentId, courseId);
        // ensure PK is set
        if (regWithUpdates.getId() == null) {
            CourseRegistration existing = service.getCourseRegistrationById(studentId, courseId);
            if (existing == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            regWithUpdates.setId(existing.getId());
        }
        CourseRegistration updated = service.updateCourseRegistration(regWithUpdates);
        return Response.ok(updated).build();
    }

    // DELETE /courseregistration/{studentId}/{courseId}
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{studentId}/{courseId}")
    public Response deleteRegistration(@PathParam("studentId") int studentId,
                                       @PathParam("courseId") int courseId) {
        LOG.debug("deleting registration for student {} course {}", studentId, courseId);
        CourseRegistration deleted = service.deleteCourseRegistration(studentId, courseId);
        if (deleted == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(deleted).build();
    }
}
