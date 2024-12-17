package com.project.config;

import com.project.service.BasketDetailsCleanupService;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class DynamicSchedulingConfig implements SchedulingConfigurer {

  private final Map<Long, ScheduledFuture<?>> activeSchedulers = new ConcurrentHashMap<>();
  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

  @Autowired
  private BasketDetailsCleanupService cleanupService;

  @Bean
  public Executor taskExecutor() {
    return executorService;
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());
  }

  public void startBasketDetailsCleanupScheduler(Long basketId, Duration expirationTime) {
    if (activeSchedulers.containsKey(basketId)) {
      return; // Scheduler is already active for this basket
    }

    ScheduledFuture<?> future = executorService.scheduleAtFixedRate(() -> {
      boolean stopScheduler = cleanupService.cleanupBasketDetails(basketId, expirationTime);
      if (stopScheduler) {
        stopBasketDetailsCleanupScheduler(basketId);
      }
    }, 0, 30, TimeUnit.SECONDS);

    activeSchedulers.put(basketId, future);
  }

  public void stopBasketDetailsCleanupScheduler(Long basketId) {
    ScheduledFuture<?> future = activeSchedulers.remove(basketId);
    if (future != null) {
      future.cancel(true);
    }
  }
}


