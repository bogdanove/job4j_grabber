package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    private Connection cn;

    public AlertRabbit() {
    }

    public static void main(String[] args) {
        try {
            AlertRabbit store = new AlertRabbit();
            store.init();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            AlertRabbit store = (AlertRabbit) context.getJobDetail().getJobDataMap().get("store");
            store.add(new Timestamp(System.currentTimeMillis()));
        }
    }

    private void init() {
        Properties prop = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            prop.load(in);
            Class.forName(prop.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    prop.getProperty("url"),
                    prop.getProperty("username"),
                    prop.getProperty("password")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(Timestamp time) {
        try (PreparedStatement ps = cn.prepareStatement("insert into rabbit(created_date) values (?)")) {
            ps.setTimestamp(1, time);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
