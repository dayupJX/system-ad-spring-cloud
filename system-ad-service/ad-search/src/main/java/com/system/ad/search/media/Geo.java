package com.system.ad.search.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Geo {

    private Float latitude;
    private Float longitude;

    private String city;
    private String province;
}
