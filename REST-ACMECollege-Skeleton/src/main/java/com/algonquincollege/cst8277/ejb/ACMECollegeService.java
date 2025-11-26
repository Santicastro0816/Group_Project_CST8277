/********************************************************************************************************
 * File:  ACMECollegeService.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.ejb;

import static com.algonquincollege.cst8277.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_KEY_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_SALT_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PREFIX;
import static com.algonquincollege.cst8277.utility.MyConstants.PARAM1;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_KEY_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_SALT_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.PU_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.CourseRegistrationPK;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.SecurityRole;
import com.algonquincollege.cst8277.entity.SecurityUser;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    private static final String READ_ALL_PROGRAMS = "SELECT name FROM program";
    
    // Query constants
    public static final String QUERY_ALL_COURSES = "Course.findAll";
    public static final String QUERY_ALL_PROFESSORS = "Professor.findAll";
    public static final String QUERY_ALL_STUDENT_CLUBS = "StudentClub.findAll";
    public static final String QUERY_ALL_COURSE_REGISTRATIONS = "CourseRegistration.findAll";
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Student> getAllStudents() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        cq.select(cq.from(Student.class));
        return em.createQuery(cq).getResultList();
    }

    public Student getStudentById(int id) {
        return em.find(Student.class, id);
    }

    @Transactional
    public Student persistStudent(Student newStudent) {
        em.persist(newStudent);
        return newStudent;
    }

    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        SecurityUser userForNewStudent = new SecurityUser();
        userForNewStudent.setUsername(
            DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewStudent.setPwHash(pwHash);
        userForNewStudent.setStudent(newStudent);
        TypedQuery<SecurityRole> roleQuery = em.createNamedQuery(SecurityRole.SECURITY_ROLE_BY_NAME, SecurityRole.class);
        roleQuery.setParameter(PARAM1, USER_ROLE);
        SecurityRole userRole = roleQuery.getSingleResult();
        userForNewStudent.getRoles().add(userRole);
        userRole.getUsers().add(userForNewStudent);
        em.persist(userForNewStudent);
    }

    /**
     * To update a student
     * 
     * @param id - id of entity to update
     * @param studentWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
    	Student studentToBeUpdated = getStudentById(id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            em.merge(studentWithUpdates);
            em.flush();
        }
        return studentWithUpdates;
    }

    /**
     * To delete a student by id
     * 
     * @param id - student id to delete
     */
    @Transactional
    public Student deleteStudentById(int id) {
        Student student = getStudentById(id);
        if (student != null) {
            em.refresh(student);
            TypedQuery<SecurityUser> findUser = em.createNamedQuery(SecurityUser.SECURITY_USER_BY_STUDENT_ID, SecurityUser.class);
            findUser.setParameter(PARAM1, id);
            SecurityUser sUser = findUser.getSingleResult();
            if (sUser != null) {
                em.remove(sUser);
            }
            em.remove(student);
        }
        return student;
    }
    
	@SuppressWarnings("unchecked")
    public List<String> getAllPrograms() {
		List<String> programs = new ArrayList<>();
		try {
			programs = (List<String>) em.createNativeQuery(READ_ALL_PROGRAMS).getResultList();
		}
		catch (Exception e) {
		}
		return programs;
    }

	// CRUD methods for Course
	public List<Course> getAllCourses() {
		TypedQuery<Course> query = em.createNamedQuery(QUERY_ALL_COURSES, Course.class);
		return query.getResultList();
	}

	public Course getCourseById(int id) {
		return em.find(Course.class, id);
	}

	@Transactional
	public Course persistCourse(Course newCourse) {
		em.persist(newCourse);
		return newCourse;
	}

	@Transactional
	public Course updateCourseById(int id, Course courseWithUpdates) {
		Course courseToBeUpdated = getCourseById(id);
		if (courseToBeUpdated != null) {
			em.refresh(courseToBeUpdated);
			em.merge(courseWithUpdates);
			em.flush();
		}
		return courseWithUpdates;
	}

	@Transactional
	public Course deleteCourseById(int id) {
		Course course = getCourseById(id);
		if (course != null) {
			em.refresh(course);
			em.remove(course);
		}
		return course;
	}

	// CRUD methods for Professor
	public List<Professor> getAllProfessors() {
		TypedQuery<Professor> query = em.createNamedQuery(QUERY_ALL_PROFESSORS, Professor.class);
		return query.getResultList();
	}

	public Professor getProfessorById(int id) {
		return em.find(Professor.class, id);
	}

	@Transactional
	public Professor persistProfessor(Professor newProfessor) {
		em.persist(newProfessor);
		return newProfessor;
	}

	@Transactional
	public Professor updateProfessorById(int id, Professor professorWithUpdates) {
		Professor professorToBeUpdated = getProfessorById(id);
		if (professorToBeUpdated != null) {
			em.refresh(professorToBeUpdated);
			em.merge(professorWithUpdates);
			em.flush();
		}
		return professorWithUpdates;
	}

	@Transactional
	public Professor deleteProfessorById(int id) {
		Professor professor = getProfessorById(id);
		if (professor != null) {
			em.refresh(professor);
			em.remove(professor);
		}
		return professor;
	}

	// CRUD methods for StudentClub
	public List<StudentClub> getAllStudentClubs() {
		TypedQuery<StudentClub> query = em.createNamedQuery(QUERY_ALL_STUDENT_CLUBS, StudentClub.class);
		return query.getResultList();
	}

	public StudentClub getStudentClubById(int id) {
		return em.find(StudentClub.class, id);
	}

	@Transactional
	public StudentClub persistStudentClub(StudentClub newStudentClub) {
		em.persist(newStudentClub);
		return newStudentClub;
	}

	@Transactional
	public StudentClub updateStudentClubById(int id, StudentClub studentClubWithUpdates) {
	    StudentClub studentClubToBeUpdated = getStudentClubById(id);
	    if (studentClubToBeUpdated != null) {
	        // Update the fields of the managed entity instead of merging
	        studentClubToBeUpdated.setName(studentClubWithUpdates.getName());
	        studentClubToBeUpdated.setDesc(studentClubWithUpdates.getDesc());
	        studentClubToBeUpdated.setAcademic(studentClubWithUpdates.getAcademic());
	        em.flush();
	        return studentClubToBeUpdated;
	    }
	    return null;
	}

	@Transactional
	public StudentClub deleteStudentClubById(int id) {
		StudentClub studentClub = getStudentClubById(id);
		if (studentClub != null) {
			em.refresh(studentClub);
			em.remove(studentClub);
		}
		return studentClub;
	}

	// CRUD methods for CourseRegistration
	public List<CourseRegistration> getAllCourseRegistrations() {
		TypedQuery<CourseRegistration> query = em.createNamedQuery(QUERY_ALL_COURSE_REGISTRATIONS, CourseRegistration.class);
		return query.getResultList();
	}

	public CourseRegistration getCourseRegistrationById(int studentId, int courseId) {
		CourseRegistrationPK pk = new CourseRegistrationPK(studentId, courseId);
		return em.find(CourseRegistration.class, pk);
	}

	@Transactional
	public CourseRegistration persistCourseRegistration(CourseRegistration newCourseRegistration) {
		em.persist(newCourseRegistration);
		return newCourseRegistration;
	}

	@Transactional
	public CourseRegistration updateCourseRegistration(CourseRegistration courseRegistrationWithUpdates) {
		em.merge(courseRegistrationWithUpdates);
		em.flush();
		return courseRegistrationWithUpdates;
	}

	@Transactional
	public CourseRegistration deleteCourseRegistration(int studentId, int courseId) {
		CourseRegistration courseRegistration = getCourseRegistrationById(studentId, courseId);
		if (courseRegistration != null) {
			em.refresh(courseRegistration);
			em.remove(courseRegistration);
		}
		return courseRegistration;
	}
	
}