package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> get(List<Long> ids, int from, int size);

    UserDto create(NewUserRequest newUserRequest);

    void deleteUserById(long id);
}
