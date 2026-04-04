package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.NewUserRequest;
import ru.practicum.ewm.dto.response.UserDto;
import ru.practicum.ewm.service.api.UserService;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) List<Long> ids,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(
                userService.getUsersDto(ids, from, size));
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.addUser(newUserRequest));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();
    }
}
