package io.github.atsin.dashr.dto;

import io.github.atsin.dashr.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    public String email;
    private Set<Role> roles;

}
