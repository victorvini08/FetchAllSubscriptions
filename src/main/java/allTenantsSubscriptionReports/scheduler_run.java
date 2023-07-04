package allTenantsSubscriptionReports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class scheduler_run {

    public static void main(String[] args) {
        SpringApplication.run(scheduler_run.class);

    }
}
