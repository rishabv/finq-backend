package com.finq.dtos.responses;

import com.finq.entities.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
@AllArgsConstructor
public class LoginResponse<T, K> {
    T details;
    String accessToken;
    Collection<K> permissions;
}
