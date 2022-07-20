package cn.smallyoung.commonsecurityspringbootstarter.base;

import cn.smallyoung.commonsecurityspringbootstarter.interfaces.DataName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @date 2020/7/25
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity<T> {

    /**
     * 创建人
     */
    @CreatedBy
    @Column(updatable=false)
    private T createdBy;

    @Transient
    private Object createdName;

    /**
     * 创建时间
     */
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @Column(updatable=false)
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @LastModifiedBy
    private T updatedBy;

    @Transient
    private Object updatedName;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime updatedTime;

    /**
     * 删除，Y：删除；N：未删除
     */
    @JsonIgnore
    @DataName(name = "删除")
    private String deleted = "N";
}
