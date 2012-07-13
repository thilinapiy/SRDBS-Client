package org.srdbs.client.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Secure and Redundant Data Backup System.
 * User: Thilina Piyasundara
 * Date: 7/10/12
 * Time: 5:35 PM
 * For more details visit : http://www.thilina.org
 */
public class RunJob  implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //TODO: validate data
    }
}
