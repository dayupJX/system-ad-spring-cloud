package com.system.ad.search.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    // 设备 id
    private String deviceId;

    // mac
    private String mac;

    // ip
    private String ip;

    // 机型编码
    private String model;

}
