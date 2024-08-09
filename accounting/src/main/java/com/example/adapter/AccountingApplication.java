package com.example.accounting;

import org.apache.camel.main.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.accounting.routes.KafkaRoute;

@SpringBootApplication
public class AccountingApplication {
	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.configure().addRoutesBuilder(new KafkaRoute());
		main.run(args);
	}
}
