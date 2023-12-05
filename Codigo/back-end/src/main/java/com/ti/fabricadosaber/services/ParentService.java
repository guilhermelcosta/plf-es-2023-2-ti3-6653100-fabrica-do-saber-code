package com.ti.fabricadosaber.services;

import com.ti.fabricadosaber.exceptions.EntityNotFoundException;
import com.ti.fabricadosaber.models.Parent;
import com.ti.fabricadosaber.models.Student;
import com.ti.fabricadosaber.repositories.ParentRepository;
import com.ti.fabricadosaber.services.exceptions.DataBindingViolationException;
import com.ti.fabricadosaber.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    public Parent findById(Long id) {
        SecurityUtil.checkUser();

        Parent parent = this.parentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Parente não encontrado! Id: " + id + ", Tipo: " + Student.class.getName()));


        return parent;
    }

    public boolean existsByCpf(String cpf) {
        return parentRepository.existsByCpf(cpf);
    }

    public Parent findByCpf(String cpf) {
        return parentRepository.findByCpf(cpf);
    }

    @Transactional
    public Parent create(Parent obj) {

        SecurityUtil.checkUser();

        obj.setId(null);
        obj.setRegistrationDate(LocalDate.now());
        return this.parentRepository.save(obj);
    }


    public Parent update(Parent obj) {
        Parent newObj = findById(obj.getId());
        String ignoredProperties = "id";

        BeanUtils.copyProperties(obj, newObj, ignoredProperties);

        return this.parentRepository.save(newObj);
    }

    public void delete(Long id) {
        Parent parent = findById(id);

        try {
            this.parentRepository.delete(parent);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas");
        }
    }

}
