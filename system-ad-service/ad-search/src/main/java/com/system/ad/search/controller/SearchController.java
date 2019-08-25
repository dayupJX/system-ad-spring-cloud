package com.system.ad.search.controller;

import com.alibaba.fastjson.JSON;

import com.system.ad.annotation.IgnoreResponseAdvice;
import com.system.ad.client.AdPlan;
import com.system.ad.client.AdPlanRequest;
import com.system.ad.client.SponsorClient;
import com.system.ad.search.service.ISearch;
import com.system.ad.search.vo.SearchRequest;
import com.system.ad.search.vo.SearchResponse;
import com.system.ad.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class SearchController {

    private final ISearch search;
    private final SponsorClient sponsorClient;

    @Autowired
    @SuppressWarnings("all")
    public SearchController(ISearch search, SponsorClient sponsorClient) {
        this.search = search;
        this.sponsorClient = sponsorClient;
    }

    @PostMapping("/findAds")
    public SearchResponse fetchAds(@RequestBody SearchRequest request) {

        log.debug("ad-search: fetchAds -> {}", JSON.toJSONString(request));
        return search.findAds(request);
    }

    @IgnoreResponseAdvice
    @PostMapping("/getAdPlans")
    public CommonResponse<List<AdPlan>> getAdPlans(@RequestBody AdPlanRequest request) {
        log.info("ad-search: getAdPlans -> {}", JSON.toJSONString(request));
        return sponsorClient.getAdPlans(request);
    }


}
