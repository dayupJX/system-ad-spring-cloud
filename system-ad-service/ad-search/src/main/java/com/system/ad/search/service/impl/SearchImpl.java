package com.system.ad.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.system.ad.index.CommonStatus;
import com.system.ad.index.adunit.AdUnitIndex;
import com.system.ad.index.adunit.AdUnitObject;
import com.system.ad.index.creative.CreativeIndex;
import com.system.ad.index.creative.CreativeObject;
import com.system.ad.index.creativeunit.CreativeUnitIndex;
import com.system.ad.index.district.UnitDistrictIndex;
import com.system.ad.index.interest.UnitItIndex;
import com.system.ad.index.keyword.UnitKeywordIndex;
import com.system.ad.search.feature.DistrictFeature;
import com.system.ad.search.feature.FeatureRelation;
import com.system.ad.search.feature.ItFeature;
import com.system.ad.search.feature.KeywordFeature;
import com.system.ad.search.media.AdSlot;
import com.system.ad.search.service.ISearch;
import com.system.ad.search.vo.SearchRequest;
import com.system.ad.search.vo.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SearchImpl implements ISearch {

    private final UnitDistrictIndex unitDistrictIndex;
    private final UnitItIndex unitItIndex;
    private final UnitKeywordIndex unitKeywordIndex;
    private final AdUnitIndex adUnitIndex;
    private final CreativeIndex creativeIndex;
    private final CreativeUnitIndex creativeUnitIndex;

    @Autowired
    public SearchImpl(AdUnitIndex adUnitIndex, UnitKeywordIndex unitKeywordIndex, UnitItIndex unitItIndex, UnitDistrictIndex unitDistrictIndex, CreativeIndex creativeIndex, CreativeUnitIndex creativeUnitIndex) {
        this.adUnitIndex = adUnitIndex;
        this.unitKeywordIndex = unitKeywordIndex;
        this.unitItIndex = unitItIndex;
        this.unitDistrictIndex = unitDistrictIndex;
        this.creativeIndex = creativeIndex;
        this.creativeUnitIndex = creativeUnitIndex;
    }

    @Override
    public SearchResponse findAds(SearchRequest request) {
        //获取查询条件
        List<AdSlot> adSlots = request.getRequestInfo().getAdSlots();
        KeywordFeature keywordFeature = request.getFeatureInfo().getKeywordFeature();
        ItFeature itFeature = request.getFeatureInfo().getItFeature();
        DistrictFeature districtFeature = request.getFeatureInfo().getDistrictFeature();
        FeatureRelation relation = request.getFeatureInfo().getRelation();

        //创建返回对象
        SearchResponse response = new SearchResponse();
        Map<String, List<SearchResponse.Creative>> adSlot2Ads = response.getAdSlot2Ads();

        for(AdSlot adSlot : adSlots) {

            //1.根据广告位进行广告单元初步筛选

            Set<Long> adUnitIdSet = adUnitIndex.findIdsByPositionType(adSlot.getPositionType());

            //2.根据限制条件进行广告单元再筛选

            if(FeatureRelation.AND == relation) {
                filterByKeywordFeature(adUnitIdSet, keywordFeature);
                filterByItFeature(adUnitIdSet, itFeature);
                filterByDistrictFeature(adUnitIdSet, districtFeature);
            } else {
                getUnitIdsByOrRelation(adUnitIdSet, keywordFeature, districtFeature, itFeature);
            }

            //3.筛选状态有效的广告单元，并通过对应关系获取广告创意
            List<AdUnitObject> unitObjects = adUnitIndex.getUnitObjectsByIds(adUnitIdSet);
                //3.1注意：除了考虑推广单元要有效，还要考虑推广单元所属的推广计划是否也有效
            filterAdUnitAndPlanByStatus(unitObjects, CommonStatus.VALID);
            Set<Long> creativeSet = creativeUnitIndex.getCreativeIdSet(unitObjects);
            List<CreativeObject> creatives = creativeIndex.getCreativeListByIds(creativeSet);
            //4.根据广告位信息对广告创意进行筛选,注意要判断创意的审核是否通过
            filterCreativesByAdSlot(creatives, adSlot.getType());

            adSlot2Ads.put(adSlot.getAdSlotId(), buildCreativeResponse(creatives));
        }

        log.debug("findAds: {}-{}",
                JSON.toJSONString(request),
                JSON.toJSONString(response));

        return response;
    }

    private List<SearchResponse.Creative> buildCreativeResponse(List<CreativeObject> creatives) {
        if (CollectionUtils.isEmpty(creatives)) {
            return Collections.emptyList();
        }

        CreativeObject randomObject = creatives.get(
                Math.abs(new Random().nextInt()) % creatives.size()
        );
        return Arrays.asList(SearchResponse.convert(randomObject));
    }

    private void filterCreativesByAdSlot(List<CreativeObject> creatives, List<Integer> typeList) {
        if (CollectionUtils.isEmpty(creatives)) return;

        CollectionUtils.filter(creatives, creative ->
                        creative.getAuditStatus().equals(CommonStatus.VALID.getStatus())
                                && typeList.contains(creative.getType())
        );
    }

    private void filterAdUnitAndPlanByStatus(List<AdUnitObject> unitObjects, CommonStatus status) {
        if (CollectionUtils.isEmpty(unitObjects)) return;

        CollectionUtils.filter(
                unitObjects,
                object -> object.getUnitStatus().equals(status.getStatus())
                        && object.getAdPlanObject().getPlanStatus().equals(status.getStatus())
        );
    }

    private void getUnitIdsByOrRelation(Set<Long> adUnitIdSet, KeywordFeature keywordFeature,
                                        DistrictFeature districtFeature, ItFeature itFeature) {
        if (CollectionUtils.isEmpty(adUnitIdSet)) {
            return;
        }

        Set<Long> keywordUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> districtUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> itUnitIdSet = new HashSet<>(adUnitIdSet);

        filterByKeywordFeature(keywordUnitIdSet, keywordFeature);
        filterByDistrictFeature(districtUnitIdSet, districtFeature);
        filterByItFeature(itUnitIdSet, itFeature);

        adUnitIdSet = new HashSet<Long>(CollectionUtils.union(
                         CollectionUtils.union(
                             keywordUnitIdSet, districtUnitIdSet), itUnitIdSet));
    }

    private void filterByDistrictFeature(Set<Long> adUnitIdSet, DistrictFeature districtFeature) {
        if(CollectionUtils.isEmpty(districtFeature.getDistricts())
                || CollectionUtils.isEmpty(adUnitIdSet)) return;

        CollectionUtils.filter(adUnitIdSet, unitId ->
                unitDistrictIndex.match(unitId, districtFeature.getDistricts())
        );
    }

    //下列三个方法可能泛型的方式描述
    private void filterByItFeature(Set<Long> adUnitIdSet, ItFeature itFeature) {
        if(CollectionUtils.isEmpty(itFeature.getIts())
                || CollectionUtils.isEmpty(adUnitIdSet)) return;

        CollectionUtils.filter(adUnitIdSet, unitId ->
                unitItIndex.match(unitId, itFeature.getIts())
        );
    }

    private void filterByKeywordFeature(Set<Long> adUnitIdSet, KeywordFeature keywordFeature) {
        if(CollectionUtils.isEmpty(keywordFeature.getKeywords())
                || CollectionUtils.isEmpty(adUnitIdSet)) return;

        CollectionUtils.filter(adUnitIdSet, unitId ->
            unitKeywordIndex.match(unitId, keywordFeature.getKeywords())
        );
    }
}
