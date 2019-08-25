package com.system.ad.search.vo;

import com.system.ad.search.feature.DistrictFeature;
import com.system.ad.search.feature.FeatureRelation;
import com.system.ad.search.feature.ItFeature;
import com.system.ad.search.feature.KeywordFeature;
import com.system.ad.search.media.AdSlot;
import com.system.ad.search.media.App;
import com.system.ad.search.media.Device;
import com.system.ad.search.media.Geo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    // 媒体方的请求Id
    private String mediaId;
    // 请求基本信息
    private RequestInfo requestInfo;
    // 匹配条件信息
    private FeatureInfo featureInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo {

        private String requestId;

        private List<AdSlot> adSlots;
        private App app;
        private Geo geo;
        private Device device;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureInfo {

        private KeywordFeature keywordFeature;
        private DistrictFeature districtFeature;
        private ItFeature itFeature;

        //默认情况下匹配条件是交集关系。
        private FeatureRelation relation = FeatureRelation.AND;
    }
}
