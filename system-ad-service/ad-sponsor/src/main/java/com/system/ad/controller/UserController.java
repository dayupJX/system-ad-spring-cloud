package com.system.ad.controller;

import com.alibaba.fastjson.JSON;
import com.system.ad.exception.AdException;
import com.system.ad.service.IUserService;
import com.system.ad.vo.UserRequest;
import com.system.ad.vo.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    private  IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create/user")
    public UserResponse createUser(@RequestBody UserRequest request) throws AdException {
        log.info("ad-sponsor: createUser -> {}", JSON.toJSONString(request));
        return userService.createUser(request);
    }
}
