package findme.dangdangcrew.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "customTaskExecutor")
    public ThreadPoolTaskExecutor customTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);  // 기본 10개 스레드
        executor.setMaxPoolSize(100);   // 최대 100개 스레드
        executor.setQueueCapacity(1000); // 최대 1000개의 요청 대기 가능
        executor.setThreadNamePrefix("SSE-Async-");
        executor.initialize();
        return executor;
    }
}

