package org.srdbs.client.scheduler;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Secure and Redundant Data Backup System.
 * User: Thilina Piyasundara
 * Date: 7/10/12
 * Time: 5:28 PM
 * For more details visit : http://www.thilina.org
 */
public class Scheduler {

    public static Logger logger = Logger.getLogger("systemsLog");

    public void Scheduler() {

        try {
            org.quartz.Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(RunJob.class)
                    .withIdentity("Backup validation Job", "Daily backup group")
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity("Backup validation trigger", "Daily backup group")
                    .startNow()
                    .withSchedule(weeklyOnDayAndHourAndMinute(6, 23, 59))
                    .build();

            scheduler.scheduleJob(job, trigger);
            logger.info("Scheduler started : weekly on Saturday at 23:59h.");
        } catch (SchedulerException e) {

           logger.info("Error on the scheduler.");
        }

    }
}