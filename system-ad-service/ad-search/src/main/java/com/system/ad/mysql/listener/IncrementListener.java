package com.system.ad.mysql.listener;

import com.system.ad.mysql.constant.Constant;
import com.system.ad.mysql.constant.OpType;
import com.system.ad.mysql.dto.BinlogRowData;
import com.system.ad.mysql.dto.MySqlRowData;
import com.system.ad.mysql.dto.TableTemplate;
import com.system.ad.sender.ISender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
//这里理解为增量数据处理器
public class IncrementListener implements Ilistener {

    private final AggregationListener aggregationListener;
    @Autowired
    @Qualifier("indexSender")
    private ISender sender;
    @Autowired
    public IncrementListener(AggregationListener aggregationListener) {
        this.aggregationListener = aggregationListener;
    }

    @Override
    @PostConstruct
    //向AggregationListener注册表对应的处理器，只有注册过的表才能被处理。
    public void register() {
        Constant.table2Db.forEach((k, v) -> {
            aggregationListener.getListenerMap().put(v + ":" + k, this);
        });
    }

    @Override
    public void onEvent(BinlogRowData binlogRowData) {
        TableTemplate table = binlogRowData.getTable();

        // 包装成最后需要投递的数据
        MySqlRowData mySqlRowData = new MySqlRowData();
        mySqlRowData.setTableName(table.getTableName());
        mySqlRowData.setLevel(binlogRowData.getTable().getLevel());
        mySqlRowData.setOpType(OpType.to(binlogRowData.getEventType()));
        mySqlRowData.setFieldValueMap(binlogRowData.getAfter());

        sender.send(mySqlRowData);

    }
}
