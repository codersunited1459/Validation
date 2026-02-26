Here are **simple but complete notes** on **Validation in Spring Boot (Jakarta Bean Validation)**, starting from basics to practical usage, with a **full working example** at the end.

---

# 📘 1. What is Validation in Spring Boot?

**Validation** means checking whether incoming data is **correct, safe, and meets business rules** before your application processes it.

👉 Example:

* Name should not be empty
* Age must be ≥ 18
* Email must be valid
* Date of birth must be in the past

---

## Why validation is important?

Without validation:

* Bad data enters DB ❌
* Security risks increase ❌
* API errors become unpredictable ❌

With validation:

* Clean API responses ✅
* Safe business logic ✅
* Better user experience ✅

---

# 📘 2. How Validation Works in Spring Boot

Spring Boot uses:

👉 **Jakarta Bean Validation API**
👉 Implemented by **Hibernate Validator**

When a request comes:

```
Client Request
      ↓
Controller (@Valid / @Validated)
      ↓
Validation Engine runs
      ↓
If errors → 400 Bad Request
Else → Controller method executes
```

---

# 📘 3. Dependency Needed

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

# 📘 4. Types of Validation in Spring Boot

There are **3 main types**:

| Type         | Used For                         |
| ------------ | -------------------------------- |
| Field-level  | Validate individual fields       |
| Class-level  | Compare multiple fields          |
| Method-level | Validate path variables & params |

---

# 📘 5. Common Validation Annotations

## Required Field

```java
@NotNull
@NotBlank
@NotEmpty
```

Difference:

| Annotation | Works On          | Rejects null | Rejects "" | Rejects " " |
| ---------- | ----------------- | ------------ | ---------- | ----------- |
| NotNull    | Any               | YES          | NO         | NO          |
| NotEmpty   | String/Collection | YES          | YES        | NO          |
| NotBlank   | String            | YES          | YES        | YES         |

👉 Use **@NotBlank** for text fields.

---

## Size & Length

```java
@Size(min=2, max=50)
```

Used for:

* String length
* List size
* Map size

---

## Numeric Validation

```java
@Min(18)
@Max(120)
@Positive
@PositiveOrZero
```

---

## Format Validation

```java
@Email
@Pattern
```

---

## Date Validation

```java
@Past
@Future
@PastOrPresent
@FutureOrPresent
```

---

## Nested Object Validation

```java
@Valid
```

Used when DTO contains another DTO.

---

## List Element Validation

```java
List<@NotBlank String> roles;
```

---

# 📘 6. Validation Groups (Create vs Update)

Groups allow **different rules for different operations**.

Example:

* Create → name required
* Update → name optional

---

### Step 1: Create Marker Interfaces

```java
public interface OnCreate {}
public interface OnUpdate {}
```

---

### Step 2: Use Groups in DTO

```java
@NotBlank(groups = OnCreate.class)
private String name;
```

---

### Step 3: Trigger Group in Controller

```java
@Validated(OnCreate.class)
```

---

# 📘 7. Custom Validation

Used when built-in annotations are not enough.

Example:

* Username should not contain spaces

Steps:

1. Create annotation
2. Create validator
3. Apply annotation

---

# 📘 8. Cross-Field Validation

Used when validation depends on multiple fields.

Example:

* Password must match Confirm Password

Implemented using **class-level annotation**.

---

# 📘 9. Path Variable & Request Param Validation

Used for GET/DELETE endpoints.

Requires:

```java
@Validated   // on controller
```

Then:

```java
@GetMapping("/{id}")
public void get(@PathVariable @Min(1) Long id)
```

---

# 📘 10. Global Exception Handling

Spring throws:

| Exception                       | When                   |
| ------------------------------- | ---------------------- |
| MethodArgumentNotValidException | Body validation fails  |
| ConstraintViolationException    | Param validation fails |

We use `@RestControllerAdvice` to handle both.

---

# 📘 11. Complete Working Example

---
Below is a **complete, end-to-end Spring Boot validation example** with **everything** in one place:

* ✅ DTO-only validation (no entities)
* ✅ Basic Jakarta validations (`@NotBlank`, `@Email`, `@Size`, `@Min`, `@Max`, `@Past`, `@Pattern`, `@NotEmpty`, `@NotNull`)
* ✅ Nested DTO validation (`@Valid`)
* ✅ List element validation (`List<@NotBlank String>`)
* ✅ Validation groups (`OnCreate`, `OnUpdate`)
* ✅ Custom annotation (`@NoWhitespace`)
* ✅ Cross-field validation (`@PasswordMatch`)
* ✅ Controller validations for path variable + request param (`@Validated` + constraints)
* ✅ Service method validation (`@Validated` on service)
* ✅ Global exception handler for both body + param validation

Assuming **Spring Boot 3.x** (`jakarta.validation.*`).

---

## 0) Dependency (Maven)

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 1) Groups

**`src/main/java/com/example/demo/validation/OnCreate.java`**

```java
package com.example.demo.validation;

public interface OnCreate {}
```

**`src/main/java/com/example/demo/validation/OnUpdate.java`**

```java
package com.example.demo.validation;

public interface OnUpdate {}
```

---

## 2) Custom annotation: `@NoWhitespace`

**`src/main/java/com/example/demo/validation/NoWhitespace.java`**

```java
package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NoWhitespaceValidator.class)
public @interface NoWhitespace {
  String message() default "must not contain whitespace";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
```

**`src/main/java/com/example/demo/validation/NoWhitespaceValidator.java`**

```java
package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoWhitespaceValidator implements ConstraintValidator<NoWhitespace, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;     // let @NotBlank handle required
    return !value.matches(".*\\s+.*");  // no spaces/tabs/newlines
  }
}
```

---

## 3) Cross-field validation: `@PasswordMatch`

**`src/main/java/com/example/demo/validation/PasswordMatch.java`**

```java
package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {
  String message() default "password and confirmPassword must match";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
```

**`src/main/java/com/example/demo/validation/PasswordMatchValidator.java`**

```java
package com.example.demo.validation;

import com.example.demo.web.dto.UserRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserRequestDto> {

  @Override
  public boolean isValid(UserRequestDto dto, ConstraintValidatorContext context) {
    if (dto == null) return true;

    String p1 = dto.getPassword();
    String p2 = dto.getConfirmPassword();

    // If missing, required checks will be handled by @NotBlank (on create group)
    if (p1 == null || p2 == null) return true;

    boolean ok = p1.equals(p2);
    if (!ok) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("confirmPassword must match password")
          .addPropertyNode("confirmPassword")
          .addConstraintViolation();
    }
    return ok;
  }
}
```

---

## 4) DTOs (Nested + List Element Validation + Groups + Custom + Cross-field)

### 4.1 Nested DTO

**`src/main/java/com/example/demo/web/dto/AddressDto.java`**

```java
package com.example.demo.web.dto;

import com.example.demo.validation.OnCreate;
import com.example.demo.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressDto {

  @NotBlank(groups = OnCreate.class, message = "line1 is required")
  @Size(max = 100, groups = {OnCreate.class, OnUpdate.class}, message = "line1 must be <= 100 chars")
  private String line1;

  @NotBlank(groups = OnCreate.class, message = "city is required")
  @Size(max = 50, groups = {OnCreate.class, OnUpdate.class}, message = "city must be <= 50 chars")
  private String city;

  // Optional but if present must match (both groups)
  @Pattern(regexp = "^[0-9]{6}$", groups = {OnCreate.class, OnUpdate.class}, message = "pincode must be 6 digits")
  private String pincode;

  public String getLine1() { return line1; }
  public void setLine1(String line1) { this.line1 = line1; }
  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }
  public String getPincode() { return pincode; }
  public void setPincode(String pincode) { this.pincode = pincode; }
}
```

### 4.2 Main Request DTO (ONE DTO with everything)

**`src/main/java/com/example/demo/web/dto/UserRequestDto.java`**

```java
package com.example.demo.web.dto;

import com.example.demo.validation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

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
```

---

## 5) Service with method validation

**`src/main/java/com/example/demo/service/UserService.java`**

```java
package com.example.demo.service;

import com.example.demo.web.dto.UserRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Validated
@Service
public class UserService {

  private final Map<Long, UserRequestDto> store = new HashMap<>();
  private final AtomicLong seq = new AtomicLong(100);

  public Long create(@Valid @NotNull UserRequestDto dto) {
    long id = seq.incrementAndGet();
    dto.setId(id);
    store.put(id, dto);
    return id;
  }

  public UserRequestDto get(@NotNull @Min(1) Long id) {
    UserRequestDto dto = store.get(id);
    if (dto == null) throw new NoSuchElementException("User not found: " + id);
    return dto;
  }

  public List<UserRequestDto> search(String q, int limit) {
    return store.values().stream().limit(limit).toList();
  }

  public void update(@NotNull @Min(1) Long id, @Valid @NotNull UserRequestDto dto) {
    if (!store.containsKey(id)) throw new NoSuchElementException("User not found: " + id);
    dto.setId(id);
    store.put(id, dto);
  }

  public void delete(@NotNull @Min(1) Long id) {
    if (store.remove(id) == null) throw new NoSuchElementException("User not found: " + id);
  }
}
```

---

## 6) Controller (CRUD + path variable + request param validations)

**`src/main/java/com/example/demo/web/UserController.java`**

```java
package com.example.demo.web;

import com.example.demo.service.UserService;
import com.example.demo.validation.NoWhitespace;
import com.example.demo.validation.OnCreate;
import com.example.demo.validation.OnUpdate;
import com.example.demo.web.dto.UserRequestDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
```

---

## 7) Global exception handler (body + param)

**`src/main/java/com/example/demo/web/ApiExceptionHandler.java`**

```java
package com.example.demo.web;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

  // Body validation errors (@RequestBody)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleBodyValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

    return ResponseEntity.badRequest().body(Map.of(
        "message", "Validation failed",
        "errors", errors
    ));
  }

  // Param validation errors (@PathVariable/@RequestParam)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleParamValidation(ConstraintViolationException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));

    return ResponseEntity.badRequest().body(Map.of(
        "message", "Validation failed",
        "errors", errors
    ));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
  }
}
```

---

## 8) Quick test payloads

### ✅ Create (POST `/api/users`)

```json
{
  "name": "Raghav",
  "email": "raghav@example.com",
  "username": "raghav_01",
  "age": 25,
  "dateOfBirth": "1999-01-01",
  "address": { "line1": "A-1", "city": "Hyderabad", "pincode": "500001" },
  "roles": ["USER"],
  "password": "password123",
  "confirmPassword": "password123"
}
```

### ❌ Cross-field fail (password mismatch)

```json
{
  "name": "Raghav",
  "email": "raghav@example.com",
  "username": "raghav 01",
  "age": 25,
  "dateOfBirth": "1999-01-01",
  "address": { "line1": "A-1", "city": "Hyderabad", "pincode": "500001" },
  "roles": ["USER"],
  "password": "password123",
  "confirmPassword": "password124"
}
```


# 📘 12. Best Practices

✔ Always validate at **DTO level**, not entity
✔ Use **groups** for create/update logic
✔ Use **@Valid** for nested objects
✔ Use **global exception handler**
✔ Never rely on frontend validation only

---

# 📘 13. Common Mistakes

❌ Using `@NotNull` on primitive types
❌ Forgetting `@Valid` for nested DTO
❌ Not adding `@Validated` for param validation
❌ Mixing entity validation with API validation

---

# 📘 14. Summary

Validation in Spring Boot allows you to:

* Ensure safe and correct input
* Apply rules declaratively using annotations
* Customize rules with groups and custom validators
* Handle errors globally in a consistent way

---



