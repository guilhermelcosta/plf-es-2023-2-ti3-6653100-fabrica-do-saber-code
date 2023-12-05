package com.ti.fabricadosaber.services;


import com.ti.fabricadosaber.dto.StudentResponseDTO;
import com.ti.fabricadosaber.dto.TeamResponseDTO;
import com.ti.fabricadosaber.dto.VacationTeamResponseDTO;
import com.ti.fabricadosaber.exceptions.DataException;
import com.ti.fabricadosaber.exceptions.EntityNotFoundException;
import com.ti.fabricadosaber.models.*;
import com.ti.fabricadosaber.repositories.VacationTeamRepository;
import com.ti.fabricadosaber.services.exceptions.DataBindingViolationException;
import com.ti.fabricadosaber.services.interfaces.TeamOperations;
import com.ti.fabricadosaber.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class VacationTeamService implements TeamOperations {

    @Autowired
    private VacationTeamRepository vacationTeamRepository;

    @Autowired
    private TeacherService teacherService;


    @Autowired
    private StudentService studentService;

    @Autowired
    @Lazy
    private StudentTeamAssociationService studentTeamAssociationService;


    @Override
    public VacationTeam findById(Long id) {
        VacationTeam team = this.vacationTeamRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Creche não encontrada! Id: " + id + ", Tipo: " + VacationTeam.class.getName()));

        SecurityUtil.checkUser();
        return team;
    }


    public VacationTeamResponseDTO findByIdDTO(Long id) {
        VacationTeam vacationTeam = findById(id);

        List<Long> studentIds = studentTeamAssociationService.findStudentIdsByVacationTeamId(id);

        VacationTeamResponseDTO teamResponseDTO = convertToTeamResponseDTO(vacationTeam, studentIds);

        return teamResponseDTO;
    }

    public List<StudentResponseDTO> listStudents(Long id) {
        VacationTeam vacationTeam = findById(id);

        List<Student> students = studentTeamAssociationService.findStudentsActiveOnVacationTeam(id);

        if(students.isEmpty()) {
            throw new EntityNotFoundException("Nenhum aluno está ativo na creche de férias");
        }

        List<StudentResponseDTO> studentResponseDTOS = new ArrayList<>();

        for (Student student: students) {
            List<Long> teamIds = studentTeamAssociationService.findTeamsAndVacationTeams(student.getId());
            StudentResponseDTO studentResponseDTO = studentService.convertToStudentResponseDTO(student, teamIds);
            studentResponseDTOS.add(studentResponseDTO);
        }


        return studentResponseDTOS;
    }


    public List<VacationTeamResponseDTO> listAllTeams() {
        SecurityUtil.checkUser();

        List<VacationTeam> vacationTeamsteams = this.vacationTeamRepository.findAllTeams();
        List<VacationTeamResponseDTO> teamDTO = new ArrayList<>();

        for (VacationTeam team : vacationTeamsteams) {
            List<Long> studentIds = studentTeamAssociationService.findStudentIdsByTeamId(team.getId());
            VacationTeamResponseDTO teamResponseDTO = convertToTeamResponseDTO(team, studentIds);
            teamDTO.add(teamResponseDTO);
        }

        if (teamDTO.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma creche de férias cadastrada");
        }

        return teamDTO;
    }



    public void checkDate(VacationTeam vacationTeam) {
        dataNotNull(vacationTeam);
        startDateValidate(vacationTeam);
        validateDateRange(vacationTeam);
    }

    public VacationTeam create(VacationTeam obj) {

        obj.setId(null);
        obj.setTeacher(checkTeacher(obj.getTeacher()));
        checkDate(obj);

        obj = this.vacationTeamRepository.save(obj);

        teamStudentsInCreate(obj);

        return obj;

    }

    public VacationTeam update(VacationTeam obj) {
        VacationTeam newObj = findById(obj.getId());
        newObj.setName(obj.getName());
        newObj.setClassroom(obj.getClassroom());
        newObj.setGrade(obj.getGrade());
        newObj.setTeacher(checkTeacher(obj.getTeacher()));
        newObj.setNumberStudents(newObj.getNumberStudents());
        newObj.setStudentIds(obj.getStudentIds());
        checkDate(obj);

        newObj = this.vacationTeamRepository.save(newObj);

        updateTeamWithStudents(newObj);

        return newObj;
    }

    public void teamStudentsInCreate(VacationTeam obj) {

        Set<Long> studentIds = obj.getStudentIds();

        if(studentIds != null && !studentIds.isEmpty()) {

            for(Long studentId : studentIds) {
                Student existingStudent = studentService.findById(studentId);

                studentTeamAssociationService.enrollStudentOnTeam(new StudentTeamAssociation(existingStudent, obj),
                        false);
            }
        }
    }

    private void updateTeamWithStudents(Team team) {
        studentTeamAssociationService.updateTeamOnAssociation(team.getStudentIds(), team);
    }


    @Override
    public Teacher checkTeacher(Teacher teacher) {
        Teacher existingTeacher = null;

        if(teacher != null)
            existingTeacher = teacherService.findById(teacher.getId());

        return existingTeacher;
    }


    public static void validateDateRange(VacationTeam vacationTeam) {
        LocalDate dataInicio = vacationTeam.getStartDate();
        LocalDate dataTermino = vacationTeam.getEndDate();

        if (dataInicio.isAfter(dataTermino)) {
            throw new DataException("A data de início não pode ser posterior à data de término.");
        }
    }

    public void dataNotNull(VacationTeam vacationTeam) {
        LocalDate dataInicio = vacationTeam.getStartDate();
        LocalDate dataTermino = vacationTeam.getEndDate();

        if(dataInicio == null || dataTermino == null) {
            throw new DataException("A data de início e término da creche de férias não pode ser nulo.");
        }
    }


    public void startDateValidate(VacationTeam vacationTeam) {
        if(!(vacationTeam.getStartDate().isAfter(LocalDate.now())))
            throw new DataException("A creche só pode começar de hoje em diante!");
    }


    public void updateTeamStudentCount(VacationTeam vacationTeam, Integer studentCount) {
        vacationTeam.setNumberStudents(studentCount);
        vacationTeamRepository.save(vacationTeam);
    }


    @Override
    public void delete(Long id) {
        VacationTeam team = findById(id);
        try {
            studentTeamAssociationService.deleteVacationTeam(id);
            this.vacationTeamRepository.delete(team);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas");
        }
    }


    public VacationTeamResponseDTO convertToTeamResponseDTO(VacationTeam team, List<Long> studentIds) {

        VacationTeamResponseDTO dto = new VacationTeamResponseDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setClassroom(team.getClassroom());
        dto.setGrade(team.getGrade());
        dto.setNumberStudents(team.getNumberStudents());

        if(team.getTeacher() == null)
            dto.setTeacherId(null);
        else
            dto.setTeacherId(team.getTeacher().getId());

        dto.setStudentIds(studentIds);
        dto.setStartDate(team.getStartDate());
        dto.setEndDate(team.getEndDate());

        return dto;
    }



}
