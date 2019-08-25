package com.system.ad.index.handle;

import com.system.ad.mysql.constant.OpType;
import com.system.ad.dump.table.*;
import com.system.ad.index.IndexAware;
import com.system.ad.index.adplan.AdPlanIndex;
import com.system.ad.index.adplan.AdPlanObject;
import com.system.ad.index.adunit.AdUnitIndex;
import com.system.ad.index.adunit.AdUnitObject;
import com.system.ad.index.creative.CreativeIndex;
import com.system.ad.index.creative.CreativeObject;
import com.system.ad.index.creativeunit.CreativeUnitIndex;
import com.system.ad.index.creativeunit.CreativeUnitObject;
import com.system.ad.index.district.UnitDistrictIndex;
import com.system.ad.index.interest.UnitItIndex;
import com.system.ad.index.keyword.UnitKeywordIndex;
import com.system.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
//将这里类比为Service层，将XXXIndex类比为DAO层？
public class AdLevelDataHandler {

    private static AdPlanIndex planIndex;
    private static CreativeIndex creativeIndex;
    private static AdUnitIndex unitIndex;
    private static CreativeUnitIndex creativeUnitIndex;
    private static UnitDistrictIndex unitDistrictIndex;
    private static UnitItIndex unitItIndex;
    private static UnitKeywordIndex unitKeywordIndex;

    @Autowired
    public void setAdIndex(AdPlanIndex adPlanIndex,
                               CreativeIndex creativeIndex,
                               AdUnitIndex unitIndex,
                               CreativeUnitIndex creativeUnitIndex,
                               UnitDistrictIndex unitDistrictIndex,
                               UnitItIndex unitItIndex,
                               UnitKeywordIndex unitKeywordIndex) {
        this.planIndex = adPlanIndex;
        this.creativeIndex = creativeIndex;
        this.unitIndex = unitIndex;
        this.creativeUnitIndex = creativeUnitIndex;
        this.unitDistrictIndex = unitDistrictIndex;
        this.unitItIndex = unitItIndex;
        this.unitKeywordIndex = unitKeywordIndex;
    }



    /**
     * service，提供 将从文件中解析出的XXXTable数据转化为XXXIndex索引中的数据 的服务
     */
    public static void handleLevel2(AdPlanTable planTable, OpType type) {
        //把直接从文件中读取出的数据转化为索引数据
        AdPlanObject planObject = new AdPlanObject(
                planTable.getId(),
                planTable.getUserId(),
                planTable.getPlanStatus(),
                planTable.getStartDate(),
                planTable.getEndDate()
        );
        //这里可理解为在  handleBinlogEvent()方法里统一调用“DAO层”的方法
        handleBinlogEvent(planIndex, planObject.getPlanId(), planObject, type);
    }

    public static void handleLevel2(AdCreativeTable creativeTable, OpType type) {

        CreativeObject creativeObject = new CreativeObject(
                creativeTable.getAdId(),
                creativeTable.getName(),
                creativeTable.getType(),
                creativeTable.getMaterialType(),
                creativeTable.getHeight(),
                creativeTable.getWidth(),
                creativeTable.getAuditStatus(),
                creativeTable.getAdUrl()
        );
        handleBinlogEvent(creativeIndex, creativeObject.getAdId(), creativeObject, type);
    }

    public static void handleLevel3(AdUnitTable unitTable, OpType type) {
        AdPlanObject adPlanObject = planIndex.get(unitTable.getPlanId());
        if (adPlanObject == null) {
            log.error("handleLevel3 found AdPlanObject error {} ",unitTable.getPlanId());
            return;
        }
        AdUnitObject unitObject = new AdUnitObject(
                unitTable.getUnitId(),
                unitTable.getUnitStatus(),
                unitTable.getPositionType(),
                unitTable.getPlanId(),
                adPlanObject
        );
        handleBinlogEvent(unitIndex, unitTable.getUnitId(),unitObject,type);
    }

    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable, OpType type) {

        AdUnitObject unitObject = unitIndex.get(creativeUnitTable.getUnitId());
        CreativeObject creativeObject = creativeIndex.get(creativeUnitTable.getAdId());
        if (unitObject == null || creativeObject == null) {
            log.error("CreativeUnitTable can not find unitObject or creativeObject");
            return;
        }

        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(
                creativeUnitTable.getAdId(),
                creativeUnitTable.getUnitId()
        );
        handleBinlogEvent(
                creativeUnitIndex,
                CommonUtils.stringConcat(
                        creativeUnitObject.getAdId().toString(),
                        creativeUnitObject.getUnitId().toString()
                ),
                creativeUnitObject,
                type
        );
    }

    public static void handleLevel4(AdUnitDistrictTable unitDistrictTable, OpType type) {

        /*if (type == OpType.UPDATE) {
            log.error("district index can not support update");
            return;
        }*/

        AdUnitObject unitObject = unitIndex .get(unitDistrictTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitDistrictTable index error: {}", unitDistrictTable.getUnitId());
            return;
        }

        String key = CommonUtils.stringConcat(
                unitDistrictTable.getProvince(),
                unitDistrictTable.getCity()
        );


        Set<Long> value = new HashSet<>();
        value.add(unitDistrictTable.getUnitId());
        handleBinlogEvent(
                unitDistrictIndex,
                key, value,
                type
        );
    }

    public static void handleLevel4(AdUnitItTable unitItTable, OpType type) {

        /*if (type == OpType.UPDATE) {
            log.error("it index can not support update");
            return;
        }*/

        AdUnitObject unitObject = unitIndex.get(unitItTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitItTable index error: {}", unitItTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>();
        value.add(unitItTable.getUnitId());
        handleBinlogEvent(unitItIndex, unitItTable.getItTag(), value, type);
    }

    public static void handleLevel4(AdUnitKeywordTable keywordTable, OpType type) {

       /* if (type == OpType.UPDATE) {
            log.error("keyword index can not support update");
            return;
        }*/

        AdUnitObject unitObject = unitIndex.get(keywordTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitKeywordTable index error: {}", keywordTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>();
        value.add(keywordTable.getUnitId());
        handleBinlogEvent(unitKeywordIndex, keywordTable.getKeyword(), value, type
        );
    }

    private static <K,V> void handleBinlogEvent(IndexAware<K,V> index, K key, V value, OpType type) {
        switch (type) {
            case ADD: index.add(key,value); break;
            case UPDATE: index.update(key, value);break;
            case DELETE: index.delete(key, value);break;
            default: break;
        }
    }
}
