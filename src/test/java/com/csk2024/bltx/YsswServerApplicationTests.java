package com.csk2024.bltx;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class YsswServerApplicationTests {

    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //$2a$10$Nlhwhtd0BSCBK95CAifv7eWpCjHloPBMZ3Gaehcc56hRAV3DZALJO
        System.out.println(encoder.encode("aaa111"));
    }

}
