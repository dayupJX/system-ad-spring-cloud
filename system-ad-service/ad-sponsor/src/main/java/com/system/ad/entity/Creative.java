package com.system.ad.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ad_creative")
public class Creative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    /** 创意的类型，可以是图片、音频等*/
    @Column(name = "type", nullable = false)
    private Integer type;

    /** 物料的类型, 比如图片可以是 bmp、jpg等 */
    @Column(name = "material_type", nullable = false)
    private Integer materialType;

    @Column(name = "height", nullable = false)
    private Integer height;

    @Column(name = "width", nullable = false)
    private Integer width;

    /** 物料大小 */
    @Basic
    @Column(name = "size", nullable = false)
    private Long size;

    /** 持续时长, 只有视频不为0 */
    @Column(name = "duration", nullable = false)
    private Integer duration;

    /** 审核状态 */
    @Column(name = "audit_status", nullable = false)
    private Integer auditStatus;

    /**创意是由用户上传的*/
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "updateTime", nullable = false)
    private Date updateTime;

}

//注：该类的构造函数可根据实际需求来编写