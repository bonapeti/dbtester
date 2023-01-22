package dbtest.config;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Configuration
@RestController
@RequestMapping(path = "/scheduling", produces = "application/json")
public class Scheduling implements SchedulingConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(Scheduling.class);
	
	private long delayInSeconds = 5;
	
	private Supplier<Long> delaySupplier = new Supplier<Long>() {

		@Override
		public Long get() {
			return delayInSeconds;
		}
	};
	
	@PostMapping(path="/delay/{delayInSeconds}")
	public ResponseEntity<Long> updateDelay(@PathVariable long delayInSeconds) {
		this.delayInSeconds = delayInSeconds;
		log.info("Updated delay, new value: {} seconds", delayInSeconds);
		return ResponseEntity.ok(this.delayInSeconds);
	}
	
	@GetMapping(path="/delay")
	public Long getDelay() {
		return this.delayInSeconds;
	}
	
	@Bean
    public Executor taskExecutor() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
	    threadPoolTaskScheduler.setPoolSize(5);
	    threadPoolTaskScheduler.setThreadNamePrefix("Timer-");
	    return threadPoolTaskScheduler;
    }

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
          new Runnable() {
              @Override
              public void run() {
            	  log.info("Run schedule");
              }
          },
          new Trigger() {
              @Override
              public Instant nextExecution(TriggerContext context) {
                  Optional<Instant> lastCompletionTime =
                    Optional.ofNullable(context.lastCompletion());
                  Instant nextExecutionTime =
                    lastCompletionTime.orElseGet(Instant::now).plusSeconds(delaySupplier.get());
                  return nextExecutionTime;
              }

          }
        );
	}
	
	
}
