package com.csk2024.bltx;

import com.csk2024.bltx.mapper.TPictureMapper;
import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.utils.PythonServerUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class YsswServerApplicationTests {

    @Resource
    TPictureMapper tPictureMapper;

    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //$2a$10$Nlhwhtd0BSCBK95CAifv7eWpCjHloPBMZ3Gaehcc56hRAV3DZALJO
        System.out.println(encoder.encode("aaa111"));
    }
    @Test
    void predict(){
        TPicture tPicture = tPictureMapper.selectByPrimaryKey(87);
        System.out.println(PythonServerUtils.predict(tPicture.getUrl()));
    }

}
