package com.system.ad.service;

import com.system.ad.exception.AdException;
import com.system.ad.vo.AdUnitDistrictRequest;
import com.system.ad.vo.AdUnitDistrictResponse;
import com.system.ad.vo.AdUnitItRequest;
import com.system.ad.vo.AdUnitItResponse;
import com.system.ad.vo.AdUnitKeywordRequest;
import com.system.ad.vo.AdUnitKeywordResponse;
import com.system.ad.vo.AdUnitRequest;
import com.system.ad.vo.AdUnitResponse;
import com.system.ad.vo.CreativeUnitRequest;
import com.system.ad.vo.CreativeUnitResponse;

public interface IAdUnitService {

    /** 创建推广单元 */
    AdUnitResponse createUnit(AdUnitRequest request) throws AdException;

    /** 关键字推广单元的创建*/
    AdUnitKeywordResponse createUnitKeyword(AdUnitKeywordRequest request) throws AdException;

    /** 兴趣推广单元的创建*/
    AdUnitItResponse createUnitIt(AdUnitItRequest request) throws AdException;

    /** 地区推广单元的创建*/
    AdUnitDistrictResponse createUnitDistrict(AdUnitDistrictRequest request) throws AdException;

    /** 推广单元和创意是多对多的关系，这里采用中间表来描述这种多对多关系*/
    CreativeUnitResponse createCreativeUnit(CreativeUnitRequest request) throws AdException;
}
