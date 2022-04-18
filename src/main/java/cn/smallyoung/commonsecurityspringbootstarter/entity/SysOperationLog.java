package cn.smallyoung.commonsecurityspringbootstarter.entity;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统操作日志
 *
 * @author smallyoung
 * @data 2020/11/4
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "t_sys_operation_log")
@EntityListeners({AuditingEntityListener.class})
@TypeDef(name = "json", typeClass = JsonStringType.class)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 2415594410963135575L;

    @Id
    @Column(name = "id" )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作人
     */
    @CreatedBy
    @Column(name = "username" )
    private String username;

    /**
     * 操作模块
     */
    @Column(name = "module" )
    private String module;

    /**
     * 操作类型
     */
    @Type(type = "json")
    @Column(name = "method" )
    private String method;

    /**
     * 请求参数
     */
    @Type(type = "json")
    @Column(name = "params" )
    private JSONObject params;

    @Column(name = "package_and_method" )
    private String packageAndMethod;

    /**
     * 操作前的数据
     */
    @Type(type = "json")
    @Column(name = "before_data" )
    private JSONObject beforeData;

    /**
     * 操作后的数据
     */
    @Type(type = "json")
    @Column(name = "after_data" )
    private JSONObject afterData;

    /**
     * 说明
     */
    @Type(type = "json")
    @Column(name = "content" )
    private JSONObject content;

    /**
     * 开始时间
     */
    @Column(name = "start_time" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime endTime;

    /**
     * 操作状态
     */
    @Column(name = "result_status" )
    private String resultStatus;

    /**
     * 操作结果
     */
    @Column(name = "result_msg" )
    private String resultMsg;

    /**
     * 服务器的ip地址
     */
    @Column(name = "server_ip" )
    private String serverIp;
}


