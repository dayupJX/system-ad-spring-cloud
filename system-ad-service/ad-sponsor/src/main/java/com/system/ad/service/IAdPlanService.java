package com.system.ad.service;

import com.system.ad.entity.AdPlan;
import com.system.ad.exception.AdException;
import com.system.ad.vo.AdPlanRequest;
import com.system.ad.vo.AdPlanResponse;
import java.util.List;

public interface IAdPlanService {

    /**创建推广计划*/
    AdPlanResponse createAdPlan(AdPlanRequest request) throws AdException;

    /**获取推广计划*/
    List<AdPlan> getAdPlanByIds(AdPlanRequest request) throws AdException;

    /**更新推广计划*/
    AdPlanResponse updateAdPlan(AdPlanRequest request) throws AdException;

    /**删除推广计划*/
    void deleteAdPlan(AdPlanRequest request) throws AdException;
}
