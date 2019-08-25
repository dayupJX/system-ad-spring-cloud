package com.system.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdPlanRequest {

    private Long id;
    private Long userId;
    private String planName;
    private String startDate;
    private String endDate;
    private List<Long> ids;

    public boolean createValidate() {

        return  userId != null
                && !StringUtils.isBlank(planName)
                && !StringUtils.isBlank(startDate)
                && !StringUtils.isBlank(endDate);
    }

    public boolean getValidate() {
        return userId != null && !CollectionUtils.isEmpty(ids);
    }

    public boolean updateValidate() {

        return id != null && userId != null;
    }

    public boolean deleteValidate() {

        return id != null && userId != null;
    }
}
