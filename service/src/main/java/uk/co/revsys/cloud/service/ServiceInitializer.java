package uk.co.revsys.cloud.service;

import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;
import uk.co.revsys.cloud.service.schedule.job.StopAllInstancesJob;

public class ServiceInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail job = newJob(StopAllInstancesJob.class).withIdentity("stop-all-instances", "cloud-service").build();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 19);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if(cal.before(new Date())){
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            Date startTime = cal.getTime();
            System.out.println("startTime = " + startTime);
            Trigger trigger = newTrigger().withIdentity("stop-all-instances", "cloud-service").startAt(startTime).withSchedule(simpleSchedule().withIntervalInHours(24).repeatForever()).build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.shutdown();
        } catch (SchedulerException ex) {
            throw new RuntimeException(ex);
        }
    }

}
