package com.mysite.sbb1.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing //자동 감지

public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImple();
    }
}
