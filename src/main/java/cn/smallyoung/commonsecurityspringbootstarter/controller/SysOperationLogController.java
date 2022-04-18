package cn.smallyoung.commonsecurityspringbootstarter.controller;

import cn.smallyoung.commonsecurityspringbootstarter.entity.SysOperationLog;
import cn.smallyoung.commonsecurityspringbootstarter.interfaces.ResponseSysResult;
import cn.smallyoung.commonsecurityspringbootstarter.service.SysOperationLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author SmallYoung
 * @date 2021/12/22
 */
@RestController
@ResponseSysResult
@RequestMapping("sysOperationLog")
public class SysOperationLogController {

    @Resource
    private SysOperationLogService sysOperationLogService;

    @GetMapping("page")
    public Page<SysOperationLog> page(@PageableDefault(sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable,
                                      HttpServletRequest request) {
        if(request.getParameter("layui") != null){
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        }
        return sysOperationLogService.findAll(WebUtils.getParametersStartingWith(request, "search_"), pageable);
    }

    @GetMapping("findById/{id}")
    public SysOperationLog findById(@PathVariable Long id) {
        return sysOperationLogService.findOne(id);
    }
}
