package com.system.ad.sender;

import com.system.ad.mysql.dto.MySqlRowData;

public interface ISender {

    void send(MySqlRowData rowData);
}
