package com.MCBS.GestiStage.service;

import com.MCBS.GestiStage.converter.TeacherDtoConverter;
import com.MCBS.GestiStage.dtos.request.TeacherDto;
import com.MCBS.GestiStage.dtos.response.SubjectDtoResponse;
import com.MCBS.GestiStage.dtos.response.TeacherDtoResponse;
import com.MCBS.GestiStage.exceptions.ApiRequestException;
import com.MCBS.GestiStage.models.Subject;
import com.MCBS.GestiStage.models.Teacher;
import com.MCBS.GestiStage.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherDtoConverter teacherDtoConverter;

    public TeacherServiceImpl(TeacherRepository teacherRepository, TeacherDtoConverter teacherDtoConverter) {
        this.teacherRepository = teacherRepository;
        this.teacherDtoConverter = teacherDtoConverter;
    }

    @Override
    public List<TeacherDtoResponse> getAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        List<TeacherDtoResponse> teacherDtoResponses = teachers.stream()
                .map(teacher-> teacherDtoConverter.convertToDto(teacher))
                .collect(Collectors.toList());
        return teacherDtoResponses;
    }

    @Override
    public TeacherDtoResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findTeacherById(id);
        if (teacher == null)
        {
            throw new ApiRequestException("Teacher dose not exist in DB!!!");
        }
        return teacherDtoConverter.convertToDto(teacher);
    }

    @Override
    public void createTeacher(TeacherDto teacherDto) {
        if (teacherRepository.findTeacherByEmail(teacherDto.getEmail())!=null)
        {
            throw new ApiRequestException("Email address is taken!");
        }
        teacherRepository.save(teacherDtoConverter.convertDtoToTeacher(teacherDto));
    }
}
