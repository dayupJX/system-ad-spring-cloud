package com.system.ad.mysql.dto;

import com.system.ad.mysql.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableTemplate {
    private String tableName;
    private String level;
    private Map<OpType, List<String>> OpTypeFieldListMap = new HashMap<>();
    //字段索引 到 字段名称的映射
    private Map<Integer, String> pos2NameMap = new HashMap<>();
}
