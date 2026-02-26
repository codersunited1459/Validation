package org.validation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.validation.validationgroups.OnCreate;
import org.validation.validationgroups.OnUpdate;

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