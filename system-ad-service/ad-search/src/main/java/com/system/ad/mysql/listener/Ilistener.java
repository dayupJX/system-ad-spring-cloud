package com.system.ad.mysql.listener;

import com.system.ad.mysql.dto.BinlogRowData;

public interface Ilistener {

    void register();

    void onEvent(BinlogRowData rowData);
}
