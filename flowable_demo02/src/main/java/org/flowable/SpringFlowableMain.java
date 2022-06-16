package org.flowable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 出差报销，首先经理审批-然后老板审批
 */
@SpringBootApplication
public class SpringFlowableMain {

    public static void main(String[] args) {
        SpringApplication.run(SpringFlowableMain.class, args);
    }
}
