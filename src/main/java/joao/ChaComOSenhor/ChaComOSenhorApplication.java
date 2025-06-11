package joao.ChaComOSenhor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChaComOSenhorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChaComOSenhorApplication.class, args);
	}
}
