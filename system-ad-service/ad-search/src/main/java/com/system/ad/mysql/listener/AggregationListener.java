package com.system.ad.mysql.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.system.ad.mysql.TemplateHolder;
import com.system.ad.mysql.dto.BinlogRowData;
import com.system.ad.mysql.dto.TableTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Data
//对mysql binlog事件进行监听，并对监听到的数据分派给相应处理器处理
public class AggregationListener implements BinaryLogClient.EventListener {

    private String dbName;
    private String tableName;
    //经过注册的数据库和表才执行相应的操作,String格式为dbname:tablename
    private Map<String, Ilistener> listenerMap = new HashMap<>();

    private TemplateHolder templateHolder;

    @Autowired
    public AggregationListener(TemplateHolder templateHolder) {
        this.templateHolder = templateHolder;
    }

    @Override
    public void onEvent(Event event) {
        EventType eventType = event.getHeader().getEventType();
        log.debug("event type is {}", eventType);
        if(eventType == EventType.TABLE_MAP) {
            //这里不需要进行强制类型转换，因为Event.getData()方法里已用泛型的方式转换好
            TableMapEventData data = event.getData();
            this.dbName = data.getDatabase();
            this.tableName = data.getTable();
            return;
        }

        //不同的mysql版本对应的EventType的表达形式不一样
        if (eventType != EventType.UPDATE_ROWS
                && eventType != EventType.WRITE_ROWS
                && eventType != EventType.DELETE_ROWS) {
            return;
        }

        // 表名和库名是否已经完成填充
        //正常情况下，EventType.UPDATE_ROWS 或者 EventType.WRITE_ROWS 或者 EventType.DELETE_ROWS
        //事件前面总有一个EventType.TABLE_MAP事件，他们组合起来描述对某一个表进行数据写操作。
        if (StringUtils.isBlank(dbName) || StringUtils.isBlank(tableName)) {
            log.error("no meta data event");
            return;
        }

        //ROW模式下binlog文件会记录所有对库表数据的写操作，这里我们只处理关心的库和表，其余不处理。
        String key = getKey(this.dbName, this.tableName);
        Ilistener listener =  listenerMap.get(key);

        if(listener == null) {
            log.debug("skip {}",key);
            return;
        }

        TableTemplate tableTemplate = templateHolder.getTableTemplate(tableName);
        if(tableTemplate == null) {
            log.debug("fail to find tableTeplate {}", tableName);
            return;
        }

        BinlogRowData rowData = new BinlogRowData();
        //rowData.setAfter()在下面这个方法里实现。
        rowData.buildRowData(event.getData(), tableTemplate);
        rowData.setEventType(eventType);
        rowData.setTable(tableTemplate);

        listener.onEvent(rowData);
        dbName = "";
        tableName = "";
    }

    private String getKey(String dbName, String tableName) {
        return dbName + ":" + tableName;
    }
}
