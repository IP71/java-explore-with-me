package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    /**
     * Method for getting info about users
     * @param ids ids of users to get info about
     * @return Method returns info about users
     */
    @GetMapping("/admin/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> get(@RequestParam(name = "ids", required = false) List<Long> ids,
                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                             @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return userService.get(ids, from, size);
    }

    /**
     * Method for creating a user
     * @param newUserRequest data for creation
     * @return Method returns the created user
     */
    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest newUserRequest) {
        return userService.create(newUserRequest);
    }

    /**
     * Method for deleting an user by id
     * @param userId id of user
     */
    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable long userId) {
        userService.deleteUserById(userId);
    }
}
