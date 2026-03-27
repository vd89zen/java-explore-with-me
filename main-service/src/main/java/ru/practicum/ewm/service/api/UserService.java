package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.request.NewUserRequest;
import ru.practicum.ewm.dto.response.UserDto;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers(List<Long> ids, int from, int size);

    List<UserDto> getUsersDto(List<Long> ids, int from, int size);

    User getUserById(Long userId);

    UserDto getUserDtoById(Long userId);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

}