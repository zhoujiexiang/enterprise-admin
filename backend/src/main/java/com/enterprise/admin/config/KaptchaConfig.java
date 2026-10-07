package com.enterprise.admin.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 图形验证码 Kaptcha 配置
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties props = new Properties();
        props.setProperty("kaptcha.border", "no");
        props.setProperty("kaptcha.textproducer.font.color", "blue");
        props.setProperty("kaptcha.textproducer.font.size", "32");
        props.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        props.setProperty("kaptcha.textproducer.char.length", "4");
        props.setProperty("kaptcha.textproducer.char.space", "6");
        props.setProperty("kaptcha.image.width", "120");
        props.setProperty("kaptcha.image.height", "40");
        props.setProperty("kaptcha.background.clear.from", "248,248,248");
        props.setProperty("kaptcha.background.clear.to", "248,248,248");
        Config config = new Config(props);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
