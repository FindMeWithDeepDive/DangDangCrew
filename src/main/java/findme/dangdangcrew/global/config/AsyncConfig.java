package findme.dangdangcrew.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "customTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);  // 기본 10개 스레드
        executor.setMaxPoolSize(50);   // 최대 50개 스레드
        executor.setQueueCapacity(100); // 최대 100개의 요청 대기 가능
        executor.setThreadNamePrefix("SSE-Async-");
        executor.initialize();
        return executor;
    }
}

