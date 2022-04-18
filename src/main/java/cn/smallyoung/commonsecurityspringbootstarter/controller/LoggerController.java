package cn.smallyoung.commonsecurityspringbootstarter.controller;

import cn.smallyoung.commonsecurityspringbootstarter.interfaces.ResponseSysResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SmallYoung
 * @date 2021/11/19
 */
@Slf4j
@RestController
@ResponseSysResult
@RequestMapping("log4j2")
public class LoggerController {

    @PostMapping("changeLevel/{level}")
    public void changeLevel(@PathVariable String level){
        log.debug("loggerPrint before debug>>>>");
        log.info("loggerPrint before info>>>>");
        log.warn("loggerPrint before warn>>>>");
        log.error("loggerPrint before error>>>>");
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(Level.toLevel(level, Level.INFO));
        ctx.updateLoggers();
        log.debug("loggerPrint after debug>>>>");
        log.info("loggerPrint after info>>>>");
        log.warn("loggerPrint after warn>>>>");
        log.error("loggerPrint after error>>>>");
    }
}
