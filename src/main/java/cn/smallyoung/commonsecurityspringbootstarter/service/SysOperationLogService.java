package cn.smallyoung.commonsecurityspringbootstarter.service;

import cn.smallyoung.commonsecurityspringbootstarter.base.BaseService;
import cn.smallyoung.commonsecurityspringbootstarter.entity.SysOperationLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author smallyoung
 * @data 2020/11/4
 */
@Service
@Transactional(readOnly = true)
public class SysOperationLogService extends BaseService<SysOperationLog, Long> {
}
