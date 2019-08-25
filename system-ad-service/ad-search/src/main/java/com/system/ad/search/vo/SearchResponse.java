package com.system.ad.search.vo;

import com.system.ad.index.creative.CreativeObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class   SearchResponse {

    //Map里的KEY对应AdSlot里的adSlotId，VALUE表示一个广告位可以有多个创意，
    //面向轮播广告的应用场景
    public Map<String, List<Creative>> adSlot2Ads = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creative {

        private Long adId;
        private String adUrl;
        //媒体方根据数据类型(图片、视频)选择解码器
        private Integer type;
        private Integer materialType;

        // 展示监测 url
        private List<String> showMonitorUrl =
                Arrays.asList("www.abc.com", "www.abc.com");
        // 点击监测 url
        private List<String> clickMonitorUrl =
                Arrays.asList("www.abc.com", "www.abc.com");
    }

    public static Creative convert(CreativeObject object) {

        Creative creative = new Creative();
        creative.setAdId(object.getAdId());
        creative.setAdUrl(object.getAdUrl());
        creative.setType(object.getType());
        creative.setMaterialType(object.getMaterialType());

        return creative;
    }
}
