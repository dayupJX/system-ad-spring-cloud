package com.system.ad.service;


import com.system.ad.exception.AdException;
import com.system.ad.vo.UserRequest;
import com.system.ad.vo.UserResponse;

public interface IUserService {

    /** 创建用户*/
    UserResponse createUser(UserRequest request) throws AdException;
}
