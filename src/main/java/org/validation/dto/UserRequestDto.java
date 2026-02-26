package org.validation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.validation.annotation.NoWhitespace;
import org.validation.annotation.PasswordMatch;
import org.validation.validationgroups.OnCreate;
import org.validation.validationgroups.OnUpdate;

import java.time.LocalDate;
import java.util.List;

@PasswordMatch(groups = OnCreate.class)
public class UserRequestDto {

  // Update: id required (Create: not required)
  @NotNull(groups = OnUpdate.class, message = "id is required for update")
  @Min(value = 1, groups = OnUpdate.class, message = "id must be >= 1")
  private Long id;

  @NotBlank(groups = OnCreate.class, message = "name is required")
  @Size(min = 2, max = 50, groups = {OnCreate.class, OnUpdate.class}, message = "name must be 2..50 characters")
  private String name;

  @NotBlank(groups = OnCreate.class, message = "email is required")
  @Email(groups = {OnCreate.class, OnUpdate.class}, message = "email must be valid")
  private String email;

  @NotBlank(groups = OnCreate.class, message = "username is required")
  @NoWhitespace(groups = {OnCreate.class, OnUpdate.class}, message = "username cannot contain spaces")
  @Size(min = 3, max = 20, groups = {OnCreate.class, OnUpdate.class}, message = "username must be 3..20 characters")
  @Pattern(regexp = "^[A-Za-z0-9_]+$", groups = {OnCreate.class, OnUpdate.class},
      message = "username can contain only letters, digits, underscore")
  private String username;

  @NotNull(groups = OnCreate.class, message = "age is required")
  @Min(value = 18, groups = {OnCreate.class, OnUpdate.class}, message = "age must be at least 18")
  @Max(value = 120, groups = {OnCreate.class, OnUpdate.class}, message = "age must be <= 120")
  private Integer age;

  @NotNull(groups = OnCreate.class, message = "dateOfBirth is required")
  @Past(groups = {OnCreate.class, OnUpdate.class}, message = "dateOfBirth must be in the past")
  private LocalDate dateOfBirth;

  // Nested validation
  @NotNull(groups = OnCreate.class, message = "address is required")
  @Valid
  private AddressDto address;

  // List + element validation
  @NotEmpty(groups = OnCreate.class, message = "roles cannot be empty on create")
  private List<@NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "role cannot be blank") String> roles;

  // Password fields required only on create (and cross-field match checked via @PasswordMatch)
  @NotBlank(groups = OnCreate.class, message = "password is required")
  @Size(min = 8, groups = OnCreate.class, message = "password must be at least 8 characters")
  private String password;

  @NotBlank(groups = OnCreate.class, message = "confirmPassword is required")
  private String confirmPassword;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public Integer getAge() { return age; }
  public void setAge(Integer age) { this.age = age; }
  public LocalDate getDateOfBirth() { return dateOfBirth; }
  public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
  public AddressDto getAddress() { return address; }
  public void setAddress(AddressDto address) { this.address = address; }
  public List<String> getRoles() { return roles; }
  public void setRoles(List<String> roles) { this.roles = roles; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  public String getConfirmPassword() { return confirmPassword; }
  public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}