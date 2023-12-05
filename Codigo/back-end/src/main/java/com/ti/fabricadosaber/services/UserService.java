package com.ti.fabricadosaber.services;


import com.ti.fabricadosaber.exceptions.EntityNotFoundException;
import com.ti.fabricadosaber.models.User;
import com.ti.fabricadosaber.models.enums.ProfileEnum;
import com.ti.fabricadosaber.repositories.UserRepository;
import com.ti.fabricadosaber.security.UserSpringSecurity;
import com.ti.fabricadosaber.services.exceptions.DataBindingViolationException;
import com.ti.fabricadosaber.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public User findById(Long id) {
        SecurityUtil.checkUser();

        Optional<User> user = this.userRepository.findById(id);

        return user.orElseThrow(() -> new EntityNotFoundException(
                "Usuário não encontrado! id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    public User findCurrentUser() {

        UserSpringSecurity userSpringSecurity = SecurityUtil.authenticated();

        Optional<User> user = this.userRepository.findById(userSpringSecurity.getId());

        return user.orElseThrow(() -> new EntityNotFoundException(
                "Usuário não encontrado!"
        ));
    }

    @Transactional
    public User create(User obj) {
        obj.setId(null);
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj.setCreateDate(LocalDate.now());
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj) {
        User existingUser = findById(obj.getId());
        String[] ignoreProperties = new String[] {"id", "profiles","createDate","password"};


        BeanUtils.copyProperties(obj, existingUser, ignoreProperties);

        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        existingUser.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        existingUser.setCreateDate(LocalDate.now());
        return this.userRepository.save(existingUser);
    }


    public void delete(Long id) {
        User user = findById(id);
        try{
            this.userRepository.delete(user);
        } catch (Exception error) {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas");
        }
    }


}
