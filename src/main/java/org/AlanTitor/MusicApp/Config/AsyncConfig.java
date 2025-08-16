package org.AlanTitor.MusicApp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "securityAwareExecutor")
    public Executor securityAwareExecutor(){
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(10);
        delegate.setMaxPoolSize(20);
        delegate.setQueueCapacity(100);
        delegate.setThreadNamePrefix("sec-exec-");
        delegate.initialize();

        return new DelegatingSecurityContextExecutor(delegate);
    }
}
