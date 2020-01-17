package com.zwl.security.uaa.dao;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassNameUserDto
 * @Description
 * @Author 二师兄
 * @Date2020-01-14 15:16
 * @Version V1.0
 **/
@Data
@Builder
public class UserDto {
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String mobile;
}
