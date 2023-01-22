package dbtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Writer  {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Writer.class, args);
	}
	
	

}
