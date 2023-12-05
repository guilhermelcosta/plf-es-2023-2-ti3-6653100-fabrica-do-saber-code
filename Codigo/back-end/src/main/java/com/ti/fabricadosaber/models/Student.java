package com.ti.fabricadosaber.models;


import com.ti.fabricadosaber.enums.Race;
import java.util.HashSet;
import java.util.Set;
import com.ti.fabricadosaber.enums.Religion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Student.TABLE_NAME)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@AttributeOverride(name = "cpf", column = @Column(nullable = true))
@AttributeOverride(name = "phoneNumber", column = @Column(nullable = true))
@AttributeOverride(name = "rg", column = @Column(nullable = true))
@AttributeOverride(name = "email", column = @Column(nullable = true))
@AttributeOverride(name = "registrationDate", column = @Column(nullable = true))
public class Student extends Person {

	public static final String TABLE_NAME = "student";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Long id;

	@Transient
	private Set<Long> teamIds = new HashSet<>();

	@Column(name = "hometown", length = 45, nullable = false, updatable = true)
	@NotBlank
	private String hometown;

	@Column(name = "nationality", length = 45, nullable = false, updatable = true)
	@NotBlank
	private String nationality;

	@Column(name = "race", length = 45, nullable = false, updatable = true)
	@Enumerated(EnumType.STRING)
	private Race race;

	@Column(name = "religion", length = 45, nullable = false, updatable = true)
	@Enumerated(EnumType.STRING)
	private Religion religion;

	@ManyToMany(cascade = CascadeType.PERSIST)
	private Set<Parent> parents = null;

}
