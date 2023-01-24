package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    private static final String SQL_INSERT = "insert into rabbit(created) values (?)";
    private static Properties property;

    public static void main(String[] args) {
        Properties pr = load();
        try (Connection cn = init(pr)) {
            int time = Integer.parseInt(pr.getProperty("rabbit.interval"));
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("cn", cn);
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
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private static Properties load() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties pr = new Properties();
            pr.load(in);
            return pr;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Connection init(Properties property) throws ClassNotFoundException, SQLException {
        Class.forName(property.getProperty("driver-class-name"));
        return DriverManager.getConnection(
                property.getProperty("url"),
                property.getProperty("username"),
                property.getProperty("password")
        );
    }

    public static void put(Connection cn) {
        try (PreparedStatement statement =
                     cn.prepareStatement(SQL_INSERT)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("cn");
            put(cn);
        }
    }
}