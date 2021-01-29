package com.dji.quartz.boot.core.aspect;

import com.dji.quartz.boot.core.annotation.QuartzLog;
import com.dji.quartz.boot.core.enums.LogStatusEnum;
import com.dji.quartz.boot.core.service.JobLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class QuartLogAspect {
    private static final int ERROR_MESSAGE_MAX_LENGTH = 900;
    @Autowired
    JobLogService jobLogService;

    @Pointcut(value = "@annotation(quartzLog)", argNames = "quartzLog")
    public void pointCut(QuartzLog quartzLog){

    }

    @Before(value = "pointCut(quartzLog)", argNames = "quartzLog")
    public void initQuartzLogContext(QuartzLog quartzLog){
        jobLogService.log(true, quartzLog.targetClass().getName(), LogStatusEnum.NORMAL.getCode(), "定时任务开始");
    }

    @AfterReturning(pointcut = "pointCut(quartzLog)", argNames = "quartzLog")
    public void afterReturning(QuartzLog quartzLog){
        jobLogService.log(LogStatusEnum.NORMAL.getCode(), "定时任务结束");
    }

    @AfterThrowing(pointcut = "pointCut(quartzLog)", throwing="ex", argNames = "ex,quartzLog")
    public void afterThrowing(Throwable ex, QuartzLog quartzLog){
        String errorMessage = ex.toString();
        errorMessage = errorMessage.length() > ERROR_MESSAGE_MAX_LENGTH ? errorMessage.substring(0, ERROR_MESSAGE_MAX_LENGTH) : errorMessage;
        jobLogService.log(LogStatusEnum.ERROR.getCode(), errorMessage);
    }
}
