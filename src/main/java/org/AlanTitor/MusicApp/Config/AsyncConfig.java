package org.AlanTitor.MusicApp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExec(){
        return Executors.newFixedThreadPool(20);
    }
}
