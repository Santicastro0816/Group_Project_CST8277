/********************************************************************************************************
 * File:  StudentClub.java Course Materials CST 8277
 *
 * @author __Teddy__ __Yap__
 *
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "student_club")
@Access(AccessType.FIELD)
@AttributeOverride(name = "id", column = @Column(name = "club_id"))
@NamedQuery(name = StudentClub.ALL_STUDENT_CLUBS_QUERY, query = "SELECT sc FROM StudentClub sc")
public class StudentClub extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_STUDENT_CLUBS_QUERY = "StudentClub.findAll";

	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	protected String name;

	@Basic(optional = true)
	@Column(name = "description", nullable = true, length = 500)
	protected String desc;

	@Basic(optional = false)
	@Column(name = "academic", columnDefinition = "BIT(1)")
	protected boolean academic;

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinTable(name = "club_membership",
	    joinColumns = @JoinColumn(name = "club_id", referencedColumnName = "club_id"),
	    inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"))
	protected Set<Student> studentMembers = new HashSet<Student>();

	@Transient
	protected boolean editable = false;

	public StudentClub() {
		super();
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

	public boolean getAcademic() {
		return academic;
	}

	public void setAcademic(boolean academic) {
		this.academic = academic;
	}

	public Set<Student> getStudentMembers() {
		return studentMembers;
	}

	public void setStudentMembers(Set<Student> studentMembers) {
		this.studentMembers = studentMembers;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudentClub[id = ").append(id).append(", name = ").append(name).append(", desc = ")
				.append(desc).append(", isAcademic = ").append(academic)
				.append(", created = ").append(created).append(", updated = ").append(updated).append(", version = ").append(version).append("]");
		return builder.toString();
	}

}