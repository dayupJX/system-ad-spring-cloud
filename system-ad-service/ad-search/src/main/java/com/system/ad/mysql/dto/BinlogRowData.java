package com.system.ad.mysql.dto;

import com.github.shyiko.mysql.binlog.event.*;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
//此类用于存储EventData里的数据，目的在于解决EventData.getRows()
// 方法返回的列数据没有对应列名称而只有对应列索引的问题。
public class BinlogRowData {

    //TableTemplate类存储有列索引到列名称的映射关系。
    private TableTemplate table;

    private EventType eventType;

    private List<Map<String, String>> after;

    //对于数据库的update操作，EventData存储有更新前的值
    private List<Map<String, String>> before;

    public void buildRowData(EventData eventData, TableTemplate table) {
        this.after = new ArrayList<>();
        for(Serializable[] value : getAfterValue(eventData)) {
            Map<String,String> valueMap = new HashMap<>();
            for(int i = 0; i < value.length; i++) {
                String colName = table.getPos2NameMap().get(i);
                //str为空表示对当前列不感兴趣
                if(colName == null) continue;
                valueMap.put(colName, value[i].toString());
            }
            this.after.add(valueMap);
        }
    }

    private List<Serializable[]> getAfterValue(EventData eventData) {
        if (eventData instanceof WriteRowsEventData) {
            return ((WriteRowsEventData) eventData).getRows();
        }

        if (eventData instanceof UpdateRowsEventData) {
            return ((UpdateRowsEventData) eventData).getRows().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }

        if (eventData instanceof DeleteRowsEventData) {
            return ((DeleteRowsEventData) eventData).getRows();
        }

        return Collections.emptyList();
    }
}
