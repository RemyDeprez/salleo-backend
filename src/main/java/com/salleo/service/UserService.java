package com.salleo.service;

import com.salleo.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto create(UserDto userDto);
    UserDto getById(Long id);
    Page<UserDto> list(Pageable pageable);
    UserDto update(Long id, UserDto userDto);
    void delete(Long id);
}

