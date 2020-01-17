package com.zwl.security.uaa.controller;

import com.zwl.security.uaa.utils.Oauth2Util;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AuthController
 * @Description
 * @Author 二师兄
 * @Date 2020-01-15 17:55
 * @Version V1.0
 **/
@RestController
public class AuthController {
    @RequestMapping("/api/public/remove")
    public String removeToken(@RequestParam String token) {
        Oauth2Util.removeTokenAccess(token, "c1", "all");
        return "ok";
    }

}
