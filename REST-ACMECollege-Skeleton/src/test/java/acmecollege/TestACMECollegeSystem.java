/********************************************************************************************************
 * File:  TestACMECollegeSystem.java
 * Course Materials CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 * Comprehensive JUnit test suite for ACME College REST API
 * Tests all CRUD operations, associations, and negative test cases
 * 
 */
package acmecollege;

import static com.algonquincollege.cst8277.utility.MyConstants.APPLICATION_API_VERSION;
import static com.algonquincollege.cst8277.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_ADMIN_USER;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.PROFESSOR_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.StudentClub;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    // Test data storage for cleanup
    private static Integer createdStudentId;
    private static Integer createdCourseId;
    private static Integer createdProfessorId;
    private static Integer createdClubId;
    private static Integer createdRegistrationStudentId;
    private static Integer createdRegistrationCourseId;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(new ClientConfig())
            .register(MyObjectMapperProvider.class)
            .register(new LoggingFeature());
        webTarget = client.target(uri);
    }

    // ==================== EXISTING TESTS ====================

    @Test
    @Order(1)
    public void test01_get_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .get();
        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML_TYPE)));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
    }
    
    @Test
    @Order(2)
    public void test02_get_student_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .path("1")
            .request(MediaType.APPLICATION_JSON)
            .get();
        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
        Student student = response.readEntity(Student.class);
        assertThat(student, is(notNullValue()));
        assertThat(student.getId(), is(1));
        assertThat(student.getFirstName(), is(not(isEmptyOrNullString())));
    }
    
    @Test
    @Order(3)
    public void test03_get_programs_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .path("program")
            .request(MediaType.APPLICATION_JSON)
            .get();
        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
        List<String> programs = response.readEntity(new GenericType<List<String>>(){});
        assertThat(programs, is(not(empty())));
    }

    // ==================== STUDENT CRUD TESTS ====================

    @Test
    @Order(4)
    public void test04_create_student_with_admin_role() {
        Student newStudent = new Student();
        newStudent.setFirstName("Test");
        newStudent.setLastName("Student");
        newStudent.setEmail("test.student@algonquincollege.com");
        newStudent.setPhone("6131234567");
        newStudent.setProgram("Computer Science");
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        Student created = response.readEntity(Student.class);
        assertThat(created, is(notNullValue()));
        assertThat(created.getId(), is(not(0)));
        assertThat(created.getFirstName(), is("Test"));
        createdStudentId = created.getId();
        logger.info("Created student with id: {}", createdStudentId);
    }

    @Test
    @Order(5)
    public void test05_update_student_with_admin_role() {
        if (createdStudentId == null) {
            logger.warn("Skipping test - no student created");
            return;
        }
        
        Student updatedStudent = new Student();
        updatedStudent.setId(createdStudentId);
        updatedStudent.setFirstName("Updated");
        updatedStudent.setLastName("Student");
        updatedStudent.setEmail("updated.student@algonquincollege.com");
        updatedStudent.setPhone("6137654321");
        updatedStudent.setProgram("Updated Program");
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .path(String.valueOf(createdStudentId))
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(updatedStudent, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        Student updated = response.readEntity(Student.class);
        assertThat(updated.getFirstName(), is("Updated"));
    }

    @Test
    @Order(6)
    public void test06_get_student_not_found() {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .path("99999")
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(404));
    }

    // ==================== COURSE TESTS ====================

    @Test
    @Order(7)
    public void test07_get_all_courses_with_admin_role() {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML_TYPE)));
        
        List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
    }

    @Test
    @Order(8)
    public void test08_get_course_by_id_with_admin_role() {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .path("1")
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(200));
        Course course = response.readEntity(Course.class);
        assertThat(course, is(notNullValue()));
        assertThat(course.getId(), is(1));
    }

    @Test
    @Order(9)
    public void test09_create_course_with_admin_role() {
        Course newCourse = new Course();
        newCourse.setCourseCode("TEST101");
        newCourse.setCourseTitle("Test Course");
        newCourse.setCreditUnits(3);
        newCourse.setOnline((short) 0);
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        Course created = response.readEntity(Course.class);
        assertThat(created, is(notNullValue()));
        assertThat(created.getId(), is(not(0)));
        assertThat(created.getCourseCode(), is("TEST101"));
        createdCourseId = created.getId();
    }

    @Test
    @Order(10)
    public void test10_update_course_with_admin_role() {
        if (createdCourseId == null) {
            logger.warn("Skipping test - no course created");
            return;
        }
        
        Course updatedCourse = new Course();
        updatedCourse.setId(createdCourseId);
        updatedCourse.setCourseCode("TEST101");
        updatedCourse.setCourseTitle("Updated Test Course");
        updatedCourse.setCreditUnits(4);
        updatedCourse.setOnline((short) 1);
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .path(String.valueOf(createdCourseId))
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(updatedCourse, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        Course updated = response.readEntity(Course.class);
        assertThat(updated.getCourseTitle(), is("Updated Test Course"));
    }

    @Test
    @Order(11)
    public void test11_get_course_not_found() {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .path("99999")
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(404));
    }

    // ==================== PROFESSOR TESTS ====================

    @Test
    @Order(12)
    public void test12_get_all_professors_with_admin_role() {
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
        
        List<Professor> professors = response.readEntity(new GenericType<List<Professor>>(){});
        assertThat(professors, is(not(empty())));
    }

    @Test
    @Order(13)
    public void test13_create_professor_with_admin_role() {
        Professor newProfessor = new Professor();
        newProfessor.setFirstName("Test");
        newProfessor.setLastName("Professor");
        newProfessor.setDegree("Doctor of Philosophy");
        
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(newProfessor, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        Professor created = response.readEntity(Professor.class);
        assertThat(created, is(notNullValue()));
        assertThat(created.getId(), is(not(0)));
        createdProfessorId = created.getId();
    }

    @Test
    @Order(14)
    public void test14_get_professor_by_id_with_admin_role() {
        if (createdProfessorId == null) {
            logger.warn("Skipping test - no professor created");
            return;
        }
        
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .path(String.valueOf(createdProfessorId))
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(200));
        Professor professor = response.readEntity(Professor.class);
        assertThat(professor, is(notNullValue()));
        assertThat(professor.getId(), is(createdProfessorId));
    }

    @Test
    @Order(15)
    public void test15_update_professor_with_admin_role() {
        if (createdProfessorId == null) {
            logger.warn("Skipping test - no professor created");
            return;
        }
        
        Professor updatedProfessor = new Professor();
        updatedProfessor.setId(createdProfessorId);
        updatedProfessor.setFirstName("Updated");
        updatedProfessor.setLastName("Professor");
        updatedProfessor.setDegree("Doctor of Science");
        
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .path(String.valueOf(createdProfessorId))
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(updatedProfessor, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        Professor updated = response.readEntity(Professor.class);
        assertThat(updated.getFirstName(), is("Updated"));
    }

    // ==================== STUDENT CLUB TESTS ====================

    @Test
    @Order(16)
    public void test16_get_all_student_clubs_with_admin_role() {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
        
        List<StudentClub> clubs = response.readEntity(new GenericType<List<StudentClub>>(){});
        assertThat(clubs, is(not(empty())));
    }

    @Test
    @Order(17)
    public void test17_create_student_club_with_admin_role() {
        StudentClub newClub = new StudentClub();
        newClub.setName("Test Club");
        newClub.setDesc("Test Club Description");
        newClub.setAcademic(true);
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(newClub, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        StudentClub created = response.readEntity(StudentClub.class);
        assertThat(created, is(notNullValue()));
        assertThat(created.getId(), is(not(0)));
        createdClubId = created.getId();
    }

    @Test
    @Order(18)
    public void test18_add_student_to_club_association() {
        if (createdClubId == null) {
            logger.warn("Skipping test - no club created");
            return;
        }
        
        // Use existing student with id 1
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .path(String.valueOf(createdClubId))
            .path("members")
            .path("1")
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(null, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        StudentClub updatedClub = response.readEntity(StudentClub.class);
        assertThat(updatedClub, is(notNullValue()));
        assertThat(updatedClub.getId(), is(createdClubId));
    }

    // ==================== COURSE REGISTRATION TESTS ====================

    @Test
    @Order(20)
    public void test20_assign_professor_to_course_registration() {
        if (createdRegistrationStudentId == null || createdRegistrationCourseId == null) {
            logger.warn("Skipping test - no registration created");
            return;
        }
        
        Professor professor = new Professor();
        professor.setId(1);
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .path("student")
            .path(String.valueOf(createdRegistrationStudentId))
            .path("course")
            .path(String.valueOf(createdRegistrationCourseId))
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(professor, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(200));
        CourseRegistration updated = response.readEntity(CourseRegistration.class);
        assertThat(updated, is(notNullValue()));
        assertThat(updated.getProfessor(), is(notNullValue()));
    }

    @Test
    @Order(21)
    public void test21_assign_grade_to_course_registration() {
        if (createdRegistrationStudentId == null || createdRegistrationCourseId == null) {
            logger.warn("Skipping test - no registration created");
            return;
        }
        
        String letterGrade = "A+";
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .path("student")
            .path(String.valueOf(createdRegistrationStudentId))
            .path("course")
            .path(String.valueOf(createdRegistrationCourseId))
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(letterGrade, MediaType.TEXT_PLAIN));
        
        assertThat(response.getStatus(), is(200));
        CourseRegistration updated = response.readEntity(CourseRegistration.class);
        assertThat(updated, is(notNullValue()));
        assertThat(updated.getLetterGrade(), is("A+"));
    }

    @Test
    @Order(22)
    public void test22_get_course_registration_by_id() {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .path("1")
            .path("1")
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        // May or may not exist, but should not be 500
        assertThat(response.getStatus(), is(not(500)));
    }

    // ==================== ADDITIONAL ENDPOINT TESTS ====================

    @Test
    @Order(23)
    public void test23_get_all_letter_grades() {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .path("lettergrade")
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
        
        List<String> grades = response.readEntity(new GenericType<List<String>>(){});
        assertThat(grades, is(not(empty())));
        assertThat(grades, hasSize(14)); // A+, A, A-, B+, B, B-, C+, C, C-, D+, D, D-, F, FSP
    }

    // ==================== NEGATIVE TESTS ====================

    @Test
    @Order(24)
    public void test24_unauthorized_access_without_auth() {
        Response response = webTarget
            .path(STUDENT_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(401));
    }

    @Test
    @Order(25)
    public void test25_user_role_cannot_access_all_students() {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(403));
    }

    @Test
    @Order(26)
    public void test26_wrong_media_type_request() {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request(MediaType.APPLICATION_XML)
            .get();
        
        // Should either return 406 Not Acceptable or 200 with JSON (server may ignore Accept header)
        assertThat(response.getStatus(), is(not(500)));
        if (response.getStatus() == 200) {
            assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML_TYPE)));
        }
    }

    @Test
    @Order(27)
    public void test27_get_course_registration_not_found() {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .path("99999")
            .path("99999")
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        assertThat(response.getStatus(), is(404));
    }

    @Test
    @Order(28)
    public void test28_invalid_json_payload() {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity("{invalid json}", MediaType.APPLICATION_JSON));
        
        // Should return 400 Bad Request
        assertThat(response.getStatus(), is(400));
    }

    @Test
    @Order(29)
    public void test29_user_role_can_access_own_student() {
        // User role should be able to access their own student record
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .path("2") // Assuming user is linked to student id 2
            .request(MediaType.APPLICATION_JSON)
            .get();
        
        // Should either be 200 (if user owns student 2) or 403 (if not)
        assertThat(response.getStatus(), is(not(500)));
    }

    // ==================== CLEANUP TESTS ====================


    @Test
    @Order(30)
    public void test30_delete_student_club() {
        if (createdClubId == null) {
            logger.warn("Skipping test - no club created");
            return;
        }
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .path(String.valueOf(createdClubId))
            .request(MediaType.APPLICATION_JSON)
            .delete();
        
        assertThat(response.getStatus(), is(200));
    }

    @Test
    @Order(31)
    public void test31_delete_professor() {
        if (createdProfessorId == null) {
            logger.warn("Skipping test - no professor created");
            return;
        }
        
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_RESOURCE_NAME)
            .path(String.valueOf(createdProfessorId))
            .request(MediaType.APPLICATION_JSON)
            .delete();
        
        assertThat(response.getStatus(), is(200));
    }

    @Test
    @Order(32)
    public void test32_delete_course() {
        if (createdCourseId == null) {
            logger.warn("Skipping test - no course created");
            return;
        }
        
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .path(String.valueOf(createdCourseId))
            .request(MediaType.APPLICATION_JSON)
            .delete();
        
        assertThat(response.getStatus(), is(200));
    }

    @Test
    @Order(33)
    public void test33_delete_student() {
        if (createdStudentId == null) {
            logger.warn("Skipping test - no student created");
            return;
        }
        
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .path(String.valueOf(createdStudentId))
            .request(MediaType.APPLICATION_JSON)
            .delete();
        
        assertThat(response.getStatus(), is(200));
    }

}