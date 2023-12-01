package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.exception.UserNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> get(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from/size, size);
        List<User> foundUsers;
        if (ids == null) {
            foundUsers = userRepository.findAllBy(pageRequest);
        } else {
            foundUsers = userRepository.findAllByIdIn(ids, pageRequest);
        }
        log.info("{} users were found", foundUsers.size());
        return UserMapper.toUserDto(foundUsers);
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        User user = userRepository.save(UserMapper.newUserRequestToUser(newUserRequest));
        log.info("Created user: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
        log.info("User with id={} was deleted", id);
    }
}
