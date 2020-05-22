package com.seckill;

import com.seckill.dataobject.UserInfoDO;
import com.seckill.dao.UserInfoMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(scanBasePackages = {"com.seckill"})
@MapperScan("com.seckill.mapper")
public class SecondKillApplication
{
    public static void main( String[] args ) {
        SpringApplication.run(SecondKillApplication.class, args);
    }
}
