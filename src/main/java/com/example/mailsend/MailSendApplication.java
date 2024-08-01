package com.example.mailsend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.example.mailsend"
                        ,"com.example.config"}
)
public class MailSendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailSendApplication.class, args);
    }

}
