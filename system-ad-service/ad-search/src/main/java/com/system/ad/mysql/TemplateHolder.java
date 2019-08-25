package com.system.ad.mysql;

import com.alibaba.fastjson.JSON;
import com.system.ad.mysql.constant.OpType;
import com.system.ad.mysql.dto.ParseTemplate;
import com.system.ad.mysql.dto.TableTemplate;
import com.system.ad.mysql.dto.Template;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Component
@Data
@Slf4j
//模板持有者，准备持有模板前的一些列动作，如加载文件，完善模板等。
public class TemplateHolder {

    //ParseTemplate的静态方法parse()对Template进行解析，该类对象也存储解析后的数据
    //ParseTemplate不是bean
    private JdbcTemplate jdbcTemplate;
    private ParseTemplate parseTemplate;
    private String SQL_SCHEMA = "select table_schema, table_name, " +
            "column_name, ordinal_position from information_schema.columns " +
            "where table_schema = ? and table_name = ?";

    @Autowired
    public TemplateHolder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void init() {
        loadJson("template.json");
    }

    public TableTemplate getTableTemplate(String name) {
        return parseTemplate.getTableTemplateMap().get(name);
    }

    private void loadJson(String path) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        try {
            Template  template = JSON.parseObject(inputStream, Charset.defaultCharset(), Template.class);
            parseTemplate = ParseTemplate.parse(template);
            //完善TableTemplate里字段索引到字段名称的映射
            loadMeta();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("fail to parse json file");
        }

    }

    private void loadMeta() {
        for(Map.Entry<String, TableTemplate> entry : parseTemplate.getTableTemplateMap().entrySet()) {
            TableTemplate tableTemplate = entry.getValue();
            List<String> updateFields = tableTemplate.getOpTypeFieldListMap().get(OpType.UPDATE);
            List<String> insertFields = tableTemplate.getOpTypeFieldListMap().get(OpType.ADD);
            List<String> deleteFields = tableTemplate.getOpTypeFieldListMap().get(OpType.DELETE);

            jdbcTemplate.query(SQL_SCHEMA,new Object[] {
                    parseTemplate.getDatabase(),
                    tableTemplate.getTableName()},
                    (rs, i) -> {
                        int pos = rs.getInt("ORDINAL_POSITION");
                        String colName = rs.getString("COLUMN_NAME");
                        if ((updateFields != null && updateFields.contains(colName))
                                || (insertFields !=null && insertFields.contains(colName))
                                || (deleteFields !=null && deleteFields.contains(colName))) {
                            //mysql数据库中字段索引值是从1开始的，而BinaryLogClient类监听到的字段索引
                            //是从0开始的，因此这里需要进行转化。
                            tableTemplate.getPos2NameMap().put(pos - 1, colName);
                        }

                        return null;
                    });

        }
    }
}
