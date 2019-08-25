package com.system.ad.service.impl;

import com.system.ad.dao.CreativeRepository;
import com.system.ad.entity.Creative;
import com.system.ad.service.ICreativeService;
import com.system.ad.vo.CreativeRequest;
import com.system.ad.vo.CreativeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeServiceImpl implements ICreativeService {

    private final CreativeRepository creativeRepository;

    @Autowired
    public CreativeServiceImpl(CreativeRepository creativeRepository) {
        this.creativeRepository = creativeRepository;
    }

    @Override
    public CreativeResponse createCreative(CreativeRequest request) {

        Creative creative = creativeRepository.save(request.convertToEntity());
        return new CreativeResponse(creative.getId(), creative.getName());
    }
}
