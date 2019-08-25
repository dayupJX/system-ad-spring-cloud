package com.system.ad.service.impl;

import com.system.ad.constant.Constants;
import com.system.ad.dao.AdUserRepository;
import com.system.ad.entity.AdUser;
import com.system.ad.exception.AdException;
import com.system.ad.service.IUserService;
import com.system.ad.utils.CommonUtils;
import com.system.ad.vo.UserResponse;
import com.system.ad.vo.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final AdUserRepository userRepository;

    @Autowired
    public UserServiceImpl(AdUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) throws AdException {
        if (!request.validate()) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        AdUser oldUser = userRepository.findByUsername(request.getUsername());
        if (oldUser != null) {
            throw new AdException(Constants.ErrorMsg.SAME_NAME_User_ERROR);
        }

        AdUser newUser = userRepository.save(new AdUser(request.getUsername(),
                CommonUtils.md5(request.getUsername())
        ));

        return new UserResponse(newUser.getId(), newUser.getUsername(), newUser.getToken(),
                newUser.getCreateTime(), newUser.getUpdateTime()
        );
    }
}
