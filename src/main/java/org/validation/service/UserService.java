package org.validation.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.validation.dto.UserRequestDto;

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