package com.perficient.library.schedule;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

public abstract class DynamicScheduleConfigurer implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(DynamicScheduleConfigurer.class);

    private static final String JOB_ENABLE_TEMPLATE = "[%s] Job enabled";

    private static final String JOB_DISABLE_TEMPLATE = "[%s] Job disabled";

    private static final String TIME_LOG_TEMPLATE = "[%s] Job start executing, current execution time: %s";

    private String expression;

    private boolean active;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        // if is not active, will not add task
        if (!active) {
            logger.info(String.format(JOB_DISABLE_TEMPLATE, getJobName()));
            return;
        }
        logger.info(String.format(JOB_ENABLE_TEMPLATE, getJobName()));

        taskRegistrar.addTriggerTask(new Runnable() {

            @Override
            public void run() {
                String timeLog = String.format(TIME_LOG_TEMPLATE, getJobName(),
                    DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()));
                logger.debug(timeLog);
                execute();
            }

        }, new Trigger() {

            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                CronTrigger trigger = new CronTrigger(expression);
                Date nextExecutionTime = trigger.nextExecutionTime(triggerContext);
                return nextExecutionTime;
            }

        });
    }

    protected abstract void execute();

    protected abstract String getJobName();

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
