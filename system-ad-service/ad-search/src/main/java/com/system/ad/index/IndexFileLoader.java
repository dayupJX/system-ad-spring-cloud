package com.system.ad.index;

import com.alibaba.fastjson.JSON;
import com.system.ad.index.adplan.AdPlanIndex;
import com.system.ad.mysql.constant.OpType;
import com.system.ad.dump.DConstant;
import com.system.ad.dump.table.*;
import com.system.ad.index.handle.AdLevelDataHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
/*@DependsOn({"adPlanIndex", "creativeIndex","adUnitIndex", "creativeUnitIndex",
            "unitDistrictIndex","unitItIndex","unitKeywordIndex"})*/
//将这里类比为建立增量索引的Controller层
//此类用来建立全量索引
public class IndexFileLoader {
    @Autowired
    AdPlanIndex adPlanIndex;

    @PostConstruct
    public void init() {
        //框架启动加载AdPlanIndex(第二层级索引)
        List<String> adPlanStringList = loadDumpData(String.format("%s%s",DConstant.DATA_ROOT_DIR,DConstant.AD_PLAN));
        adPlanStringList.forEach(p -> AdLevelDataHandler.handleLevel2(JSON.parseObject(p, AdPlanTable.class), OpType.ADD));

        //框架启动加载CreativeIndex(第二层级索引)
        List<String> adCreativeStringList = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE));
        adCreativeStringList.forEach(p -> AdLevelDataHandler.handleLevel2(JSON.parseObject(p, AdCreativeTable.class), OpType.ADD));

        //框架启动加载AdUnitIndex(第三层级索引)
        List<String> adUnitStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT));
        adUnitStrings.forEach(p -> AdLevelDataHandler.handleLevel3(JSON.parseObject(p, AdUnitTable.class), OpType.ADD));

        //框架启动加载CreativeUnitIndex(第三层级索引)
        List<String> adCreativeUnitStrings = loadDumpData(String.format("%s%s",DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE_UNIT));
        adCreativeUnitStrings.forEach(p -> AdLevelDataHandler.handleLevel3(JSON.parseObject(p, AdCreativeUnitTable.class), OpType.ADD));

        //框架启动加载UnitDistrictIndex(第四层级索引)
        List<String> adUnitDistrictStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_DISTRICT));
        adUnitDistrictStrings.forEach(d -> AdLevelDataHandler.handleLevel4(JSON.parseObject(d, AdUnitDistrictTable.class), OpType.ADD));

        //框架启动加载UnitInterestIndex(第四层级索引)
        List<String> adUnitItStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_IT));
        adUnitItStrings.forEach(i -> AdLevelDataHandler.handleLevel4(JSON.parseObject(i, AdUnitItTable.class), OpType.ADD));

        //框架启动加载UnitKeywordIndex(第四层级索引)
        List<String> adUnitKeywordStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_KEYWORD));
        adUnitKeywordStrings.forEach(k -> AdLevelDataHandler.handleLevel4(JSON.parseObject(k, AdUnitKeywordTable.class), OpType.ADD));
    }



    private List<String> loadDumpData(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
           return br.lines().collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
