package net.uncrash;

import net.uncrash.logging.aop.EnableAccessLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * User service for Uncrash
 *
 * @author Sendya
 * @author Acris
 * @date 2019/04/13
 */
@EnableAccessLogger
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
