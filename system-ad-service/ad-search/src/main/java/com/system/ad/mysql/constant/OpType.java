package com.system.ad.mysql.constant;

import com.github.shyiko.mysql.binlog.event.EventType;

public enum OpType {
    ADD,
    UPDATE,
    DELETE,
    OTHER;

    public static OpType to(EventType eventType) {

        switch (eventType) {
            case WRITE_ROWS: return ADD;
            case EXT_UPDATE_ROWS: return UPDATE;
            case EXT_DELETE_ROWS: return DELETE;
            default: return OTHER;
        }
    }
}
