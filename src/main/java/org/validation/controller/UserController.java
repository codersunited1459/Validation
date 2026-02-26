package org.validation.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.validation.annotation.NoWhitespace;
import org.validation.dto.UserRequestDto;
import org.validation.service.UserService;
import org.validation.validationgroups.OnCreate;
import org.validation.validationgroups.OnUpdate;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // POST /api/users (create)
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public String create(@Validated(OnCreate.class) @RequestBody UserRequestDto dto) {
    Long id = userService.create(dto);
    return "created id=" + id;
  }

  // GET /api/users/{id} (path variable validation)
  @GetMapping("/{id}")
  public UserRequestDto get(@PathVariable @Min(1) Long id) {
    return userService.get(id);
  }

  // GET /api/users?q=abc&limit=10 (request param validation)
  @GetMapping
  public List<UserRequestDto> search(
      @RequestParam @NotBlank(message = "q is required") String q,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit
  ) {
    return userService.search(q, limit);
  }

  // PUT /api/users/{id} (update group)
  @PutMapping("/{id}")
  public String update(
      @PathVariable @Min(1) Long id,
      @Validated(OnUpdate.class) @RequestBody UserRequestDto dto
  ) {
    userService.update(id, dto);
    return "updated id=" + id;
  }

  // DELETE /api/users/{id}
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable @Min(1) Long id) {
    userService.delete(id);
  }

  // Example: custom annotation on request param too
  // GET /api/users/by-username?username=abc_1
  @GetMapping("/by-username")
  public String byUsername(
      @RequestParam
      @NotBlank(message = "username is required")
      @NoWhitespace(message = "username cannot contain spaces")
      String username
  ) {
    return "lookup username=" + username;
  }
}