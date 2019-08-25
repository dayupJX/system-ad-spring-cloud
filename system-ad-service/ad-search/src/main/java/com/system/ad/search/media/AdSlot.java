package com.system.ad.search.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSlot {

    // 广告位Id
    private String adSlotId;

    // 广告位置类型
    private Integer positionType;

    // 广告物料类型: 图片, 视频
    private List<Integer> type;

}
