package com.system.ad.mysql.dto;

import com.system.ad.mysql.constant.OpType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ParseTemplate {
    private String database;
    private Map<String, TableTemplate> tableTemplateMap = new HashMap<>();

    public static ParseTemplate parse(Template template) {
        ParseTemplate parseTemplate = new ParseTemplate();
        parseTemplate.setDatabase(template.getDatabase());
        for(JsonTable jsonTable : template.getTableList()) {
            TableTemplate tableTemplate = new TableTemplate();
            tableTemplate.setTableName(jsonTable.getTableName());
            tableTemplate.setLevel(jsonTable.getLevel());
            parseTemplate.getTableTemplateMap().put(jsonTable.getTableName(), tableTemplate);

            Map<OpType, List<String>> opTypeFieldListMap = tableTemplate.getOpTypeFieldListMap();

            for(JsonTable.Column column : jsonTable.getInsert()) {
                List<String> list = opTypeFieldListMap.computeIfAbsent(OpType.ADD, k -> new ArrayList<String>());
                list.add(column.getColumn());
            }

            for(JsonTable.Column column : jsonTable.getUpdate()) {
                List<String> list = opTypeFieldListMap.computeIfAbsent(OpType.UPDATE, k -> new ArrayList<String>());
                list.add(column.getColumn());
            }

            for(JsonTable.Column column : jsonTable.getUpdate()) {
                List<String> list = opTypeFieldListMap.computeIfAbsent(OpType.DELETE, k -> new ArrayList<String>());
                list.add(column.getColumn());
            }
        }

        return parseTemplate;
    }
}
