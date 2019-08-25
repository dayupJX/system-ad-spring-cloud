package com.system.ad.service;

import com.system.ad.vo.CreativeRequest;
import com.system.ad.vo.CreativeResponse;

public interface ICreativeService {

    CreativeResponse createCreative(CreativeRequest request);
}
